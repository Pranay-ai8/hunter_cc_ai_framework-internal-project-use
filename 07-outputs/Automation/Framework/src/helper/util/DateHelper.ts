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
