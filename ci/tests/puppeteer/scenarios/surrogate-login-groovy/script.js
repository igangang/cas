const puppeteer = require('puppeteer');
const assert = require('assert');

const cas = require('../../cas.js');

(async () => {
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await impersonate(page, "casuser1");
    await impersonate(page, "casuser2");

    console.log("Checking empty list of authorized accounts for user...");
    await cas.goto(page, "https://localhost:8443/cas/login?locale=en&service=https://apereo.github.io");
    await cas.loginWith(page, `+casuser3`, "Mellon");
    await page.waitForTimeout(1000);
    await cas.assertTicketParameter(page);
    await cas.goto(page, `https://localhost:8443/cas/logout`);

    console.log("Checking auto selection for unauthorized user...");
    await cas.goto(page, "https://localhost:8443/cas/login?locale=en&service=https://apereo.github.io");
    await cas.loginWith(page, `anotheruser+casuser3`, "Mellon");
    await page.waitForTimeout(1000);
    await cas.assertInnerTextStartsWith(page, "#loginErrorsPanel p", "You are not authorized to impersonate");

    console.log("Checking wildcard access for authorized user...");
    await cas.goto(page, "https://localhost:8443/cas/login?locale=en&service=https://apereo.github.io");
    await cas.loginWith(page, `+casuser4`, "Mellon");
    await page.waitForTimeout(1000);
    await cas.assertInnerTextContains(page, '#content p', "you may directly proceed to login");
    await cas.assertInnerTextContains(page, '#content p', "impersonation account selection is not allowed");

    await cas.goto(page, "https://localhost:8443/cas/login?locale=en&service=https://apereo.github.io");
    await cas.loginWith(page, `anybody+casuser4`, "Mellon");
    await page.waitForTimeout(1000);
    await cas.assertTicketParameter(page);
    await cas.goto(page, `https://localhost:8443/cas/logout`);
    
    await browser.close();
})();

async function impersonate(page, username) {
    await cas.goto(page, "https://localhost:8443/cas/login?locale=en&service=https://apereo.github.io");
    await cas.loginWith(page, `+${username}`, "Mellon");
    await page.waitForTimeout(1000);
    await cas.assertTextContent(page, "#titlePanel h2", "Choose Account");
    await cas.assertTextContentStartsWith(page, "#surrogateInfo", "You are provided with a list of accounts");
    await cas.assertVisibility(page, '#surrogateTarget');
    await cas.goto(page, `https://localhost:8443/cas/logout`);
}
