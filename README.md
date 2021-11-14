# HomeTaskTest_PaymentCore

## Implementation details

- IntelliJ IDEA
- Java programming language
- Gradle build system
- Rest Assured library for API testing
- Allure Framework for web report generation and visualization

## Test cases

Only base cases were implemented to show example of Rest Assured library usage.
Implemented test cases:
- Creation User with valid parameters
- Updating User with new email
- Creation User with duplicate parameters
- Creation User with invalid parameters
- Deletion User by its ID

Please find more information in the code comments. 

## Report generation

Allure is used for report generarion. It is integrated into Gradle build.
It is necessary to intall it first on the system manually or with help of system packet manager.
For example, `brew install allure` on Mac or `apt install allure` on Linux.

Run `./gredlew allureServe` command from the project directory right after the test case execution.
Local service will be started which opens html version of the report.

## Improvements

All tests are executed in a special order to first create an User, perform different checks and then delete it afterwards.
The better approach would be to make all test self contained with User creation and deletion with special `beforeEach` and `afterEach` steps.

Test Bearer token is hardcoded into the code. The better approach would be to ask user to provide with configuration or environment parameter.
Please tell me when you are done with tests, so I can disable test token.
