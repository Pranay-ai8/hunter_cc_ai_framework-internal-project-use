import { transports, format } from "winston";

export function options(scenarioName: string) {
    const sanitized = scenarioName.replace(/[<>:"/\\|?*]/g, '_');
    return {
        transports: [
            new transports.File({
                filename: `test-results/logs/${sanitized}/log.log`,
                level: 'info',
                format: format.combine(
                    format.timestamp({ format: 'MMM-DD-YYYY HH:mm:ss' }),
                    format.align(),
                    format.printf(info => `${info.level}: ${[info.timestamp]}: ${info.message}`)
                )
            }),
        ]
    }
};