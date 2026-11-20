// Cucumber formatter wiring Allure result output to allure-results/.
//
// NOTE (discovered live while wiring this up, not assumed from documentation): the installed
// allure-cucumberjs@3.10.2 no longer exposes a "CucumberJSAllureFormatter" class to extend — that
// was an older (v2.x) API. The current version's actual Cucumber `Formatter` subclass lives at the
// "allure-cucumberjs/reporter" sub-export. This file re-exports it so `config/cucumber.js` can keep
// referencing a local, standard-shaped path ("./src/support/allureReporter.js") without every
// consumer needing to know about the package's internal restructuring.
module.exports = require("allure-cucumberjs/reporter");
