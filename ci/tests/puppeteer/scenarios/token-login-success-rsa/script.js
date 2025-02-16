const puppeteer = require('puppeteer');
const cas = require('../../cas.js');
const assert = require('assert');

async function loginWithToken(page, service, token) {
    console.log(`Logging in with SSO token to service ${service}`);
    await cas.goto(page, "https://localhost:8443/cas/logout");
    await cas.goto(page, `https://localhost:8443/cas/login?service=${service}&token=${token}`);
    await page.waitForTimeout(1000);
    await cas.assertTicketParameter(page);
    await cas.goto(page, "https://localhost:8443/cas/login");
    await cas.assertCookie(page);
    await cas.assertInnerText(page, '#content div h2', "Log In Successful");
    await cas.logg("Login is successful");
}

(async () => {
    const service = `https://apereo.github.io`;
    const browser = await puppeteer.launch(cas.browserOptions());
    const page = await cas.newPage(browser);
    await cas.goto(page, "https://localhost:8443/cas/logout");

    console.log("Generating SSO token");
    const response = await cas.doRequest(`https://localhost:8443/cas/actuator/tokenAuth/casuser?service=${service}`,
        "POST", {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        });
    let body = JSON.parse(response);
    console.dir(body, {depth: null, colors: true});
    assert(body.registeredService.id === 1);
    await loginWithToken(page, service, body.token);

    console.log("Checking for SSO token in service validation response");
    await cas.goto(page, "https://localhost:8443/cas/logout");
    await cas.goto(page, `https://localhost:8443/cas/login?service=${service}`);
    await cas.loginWith(page);
    let ticket = await cas.assertTicketParameter(page);
    body = await cas.doRequest(`https://localhost:8443/cas/p3/serviceValidate?service=${service}&ticket=${ticket}`);
    console.log(body);
    let token = body.match(/<cas:token>(.+)<\/cas:token>/)[1];
    console.log(`SSO Token ${token}`);
    await loginWithToken(page, service, token);
    
    await browser.close();
})();
