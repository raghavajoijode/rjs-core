## Sample Mail Template:
Hi ${Name},

Greetings from ${senderName}.

Please use ${OTP} as one time pin to login. 

Team,
${TeamName}

## Encoded message
Hi%20$%7BName%7D,%0A%0AGreetings%20from%20$%7BsenderName%7D.%0A%0APlease%20use%20$%7BOTP%7D%20as%20one%20time%20pin%20to%20login.%20%0A%0ATeam,%0A$%7BTeamName%7D

## Create Template GET:
http://localhost:4502/api/subra/mailer/v1/create?title=Test OTP&content=Hi%20$%7BName%7D,%0A%0AGreetings%20from%20$%7BsenderName%7D.%0A%0APlease%20use%20$%7BOTP%7D%20as%20one%20time%20pin%20to%20login.%20%0A%0ATeam,%0A$%7BTeamName%7D
## View all Template Objects GET: http://localhost:4502/api/subra/mailer/v1/view/all
## View Template Object by ID GET: http://localhost:4502/api/subra/mailer/v1/view/DR_test-otp
## Read Email Content ID GET: http://localhost:4502/api/subra/mailer/v1/read-content/DR_test-otp
## Generate Json for ID GET: http://localhost:4502/api/subra/mailer/v1/generate-json/DR_test-otp

## Send Email using POST:
http://localhost:4502/api/subra/mailer/v1/sendemail			
body=

{
    "templateId": "DR_raghava-test",
    "params": {
        "pin": "876542",
        "subject": "Hello Test",
        "name": "Raghava",
        "xyz": "ZOM",
        "Team": "Subra",
        "to": "raghava.joijode@gmail.com,jenkins.subra@gmail.com"
    }
}