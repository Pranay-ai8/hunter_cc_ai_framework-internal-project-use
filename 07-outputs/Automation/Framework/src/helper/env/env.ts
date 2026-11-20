import * as dotenv from 'dotenv'

export const getEnv = () => {
    const env = process.env.ENV || "STG";
    console.log(`Setting environment to: ${env}`);
    
    dotenv.config({
        override: true,
        path: `src/helper/env/.env.${env}`
    });
}