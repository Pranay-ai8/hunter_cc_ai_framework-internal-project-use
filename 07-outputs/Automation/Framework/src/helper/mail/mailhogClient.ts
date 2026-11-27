import * as http from "http";

export interface MailHogMessage {
    id: string;
    to: string;
    from: string;
    subject: string;
    body: string;
}

function getBaseUrl(): string {
    const url = process.env.MAILHOG_URL;
    if (!url) {
        throw new Error("MAILHOG_URL must be set in the environment - no hardcoded fallback server address.");
    }
    return url.replace(/\/$/, "");
}

function getJson(url: string): Promise<any> {
    return new Promise((resolve, reject) => {
        http.get(url, { timeout: 15000 }, (res) => {
            let data = "";
            res.on("data", (chunk) => (data += chunk));
            res.on("end", () => {
                try {
                    resolve(JSON.parse(data));
                } catch (e) {
                    reject(e);
                }
            });
        }).on("error", reject);
    });
}

function toMailHogMessage(item: any): MailHogMessage {
    const headers = item?.Content?.Headers || {};
    return {
        id: item.ID,
        to: (headers.To || [])[0] || "",
        from: (headers.From || [])[0] || "",
        subject: (headers.Subject || [])[0] || "",
        body: item?.Content?.Body || "",
    };
}

/**
 * Polls MailHog for the newest email whose subject matches `subjectContains`, only accepting one
 * whose Date header is at or after `sinceEpochMs` — so a stale email from an earlier test run isn't
 * mistaken for the one just triggered by this scenario's action.
 */
export async function waitForLatestEmailBySubject(
    subjectContains: string,
    sinceEpochMs: number,
    timeoutMs: number = 30000
): Promise<MailHogMessage> {
    const baseUrl = getBaseUrl();
    const deadline = Date.now() + timeoutMs;
    const query = encodeURIComponent(subjectContains);

    while (Date.now() < deadline) {
        const data = await getJson(`${baseUrl}/api/v2/search?kind=containing&query=${query}&limit=10`);
        const items: any[] = data?.items || [];
        for (const item of items) {
            const dateHeader = (item?.Content?.Headers?.Date || [])[0];
            const sentAt = dateHeader ? new Date(dateHeader).getTime() : 0;
            if (sentAt >= sinceEpochMs) {
                return toMailHogMessage(item);
            }
        }
        await new Promise((r) => setTimeout(r, 2000));
    }

    throw new Error(
        `No email containing subject "${subjectContains}" arrived after ${new Date(sinceEpochMs).toISOString()} within ${timeoutMs}ms`
    );
}

/**
 * Extracts the DocuSeal "Review and Sign" link from an invite email body. Matches the `/s/<slug>`
 * signing-page path specifically (rather than the first link in the body) so the DocuSeal
 * "docuseal.com/start" marketing footer link in the same email is never picked up by mistake.
 */
export function extractSigningLink(body: string): string {
    const match = body.match(/https?:\/\/[^\s"<)]+\/s\/[^\s"<)]+/);
    if (!match) {
        throw new Error("No DocuSeal signing link (/s/<slug>) found in email body");
    }
    return match[0];
}

/**
 * Extracts the "Complete Your Profile" link from the hunter-added notification email. This email's
 * HTML escapes "=" inside its href as both quoted-printable ("=3D") and an HTML entity ("&#x3D;"), and
 * can soft-wrap the URL mid-token across a "=\r\n" line break - all three need undoing before the
 * token survives intact, or matching straight against the raw body truncates or garbles it.
 */
export function extractProfileCompletionLink(body: string): string {
    const decoded = body
        .replace(/=\r?\n/g, "")
        .replace(/=3D/g, "=")
        .replace(/&#x3D;/gi, "=")
        .replace(/&amp;/gi, "&");
    const match = decoded.match(/https?:\/\/[^\s"<)]+\/user\/new\?token=[^\s"<)&]+/);
    if (!match) {
        throw new Error("No profile-completion link (/user/new?token=...) found in email body");
    }
    return match[0];
}
