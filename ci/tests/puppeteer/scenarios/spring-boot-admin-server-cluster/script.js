const puppeteer = require('puppeteer');
const cas = require('../../cas.js');
const assert = require("assert");

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/sba");
    await cas.screenshot(page);
    await cas.loginWith(page, "s#kiooritea", "p@$$W0rd");
    await cas.screenshot(page);
    await cas.goto(page, "https://localhost:8443/cas/sba/applications/CAS");
    await page.waitForTimeout(2000);
    const count = (await page.$$('div#CAS li')).length;
    console.log(`Number of instances: ${count}`);
    assert(count === 2);

    await browser.close();
})();
