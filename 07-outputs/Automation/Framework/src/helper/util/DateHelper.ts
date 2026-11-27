export class DateHelper {
    /**
     * Parses a dynamic date string (e.g. "{today+5}", "{child_dob}") and returns it formatted as MM/DD/YYYY.
     * If the string does not match any dynamic pattern, it returns the string as is.
     * @param dateStr the dynamic date string to parse
     */
    static parse(dateStr: string): string {
        if (!dateStr || !dateStr.startsWith('{') || !dateStr.endsWith('}')) {
            return dateStr;
        }

        const keyword = dateStr.slice(1, -1).toLowerCase().trim();
        const now = new Date();
        let targetDate = new Date();

        if (keyword === 'today') {
            targetDate = now;
        } else if (keyword === 'today+auto' || /^today\+auto\+\d+$/.test(keyword)) {
            // For scenarios that create a real, persisted booking (not mocked): a fixed "{today+N}"
            // offset works once, but collides with itself on every later run that day, since the real
            // booking from the earlier run is still sitting on that exact date. This instead varies by
            // a 30-minute time window, so repeated runs get a different date, while two calls within the
            // same run (a check-in date and its check-out date, a fixed number of days after it) still
            // agree on the same base offset.
            const seed = (Math.floor(now.getTime() / (1000 * 60 * 30)) % 50) + 10; // 10-59 days out
            const extra = keyword === 'today+auto' ? 0 : parseInt(keyword.split('+auto+')[1], 10);
            targetDate.setDate(now.getDate() + seed + extra);
        } else if (keyword.startsWith('today+')) {
            const days = parseInt(keyword.replace('today+', ''), 10);
            targetDate.setDate(now.getDate() + days);
        } else if (keyword.startsWith('today-')) {
            const days = parseInt(keyword.replace('today-', ''), 10);
            targetDate.setDate(now.getDate() - days);
        } else if (keyword === 'next_month') {
            targetDate.setMonth(now.getMonth() + 1);
        } else if (keyword === 'next_year') {
            targetDate.setFullYear(now.getFullYear() + 1);
        } else if (keyword === 'child_dob') {
            targetDate.setFullYear(now.getFullYear() - 10); // 10 years old
        } else if (keyword === 'adult_dob') {
            targetDate.setFullYear(now.getFullYear() - 30); // 30 years old
        } else {
            return dateStr;
        }

        // Format as MM/DD/YYYY
        const month = String(targetDate.getMonth() + 1).padStart(2, '0');
        const day = String(targetDate.getDate()).padStart(2, '0');
        const year = targetDate.getFullYear();

        return `${month}/${day}/${year}`;
    }
}
