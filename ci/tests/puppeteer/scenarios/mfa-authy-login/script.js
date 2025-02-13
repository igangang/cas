const puppeteer = require('puppeteer');
const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/login?authn_method=mfa-authy");
    await cas.loginWith(page);
    await page.waitForTimeout(1000);
    await cas.assertVisibility(page, '#token');
    await browser.close();
})();
