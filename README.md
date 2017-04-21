# HealthForge Coding Quiz

Hello! Thanks for taking the time to do our technical test. We know these are annoying, but we need a way to assess everyone that we employ to avoid disappointment on both sides later on.

Please follow the instructions below and when you are done, put your code into a git repo you own and send us a link to it for us to assess. GitHub or BitBucket are both free for public repos.

Feel free to use as much Google-Fu as you need to (that is what we do on a daily basis) - however, you need to do the test on your own. Any outside help is not in the spirit of the test and will result in seven or eight years of bad luck, depending on which is worst for you.

**Impress us with your skills and show us what you can do! This is your chance to shine and the more we see the better.**

# Pre-requesites

You will need:

- a git client
- an IDE of your choice

# Question 1

Clone this repo and look in the `data` directory at the lab results for various patients.

**We would like you to write an application that takes the CSV file and creates
JSON output of the following format:**

    {
        "patients" : [
            {
                "id" : <uuid>,
                "firstName": <string>,
                "lastName": <string>,
                "dob": <ISO 8601 timestamp>,
                "lab_results" : [
                    {
                        "timestamp": <ISO 8601 timestamp>,
                        "profile" : {
                            "name": <string>,
                            "code": <string>
                        },
                        "panel" : [
                            {
                                "code": <string>     SNOMED code of test type
                                "label": <string>    description of the code from mapping file
                                "value": <string>    value only
                                "unit": <string>     unit string
                                "lower": <double>    lower range bound
                                "upper": <double>    upper range bound
                            },
                            {
                               ...
                            },
                            {
                               ...
                            }
                        ]
                    },
                    {
                      ...
                    },
                    {
                      ...
                    }
                ]
            },
            {
              ...
            },
            {
              ...
            }
        ]
    }

Luckily, the lab results you have been given are more structured, as they have been pre-processed from their original form:

```
Test                  Units       Value       Reference Range
Haemoglobin           g/L         176         135 - 180
Red Cell Count        x10*12/L    5.9         4.2 - 6.0
Haematocrit                       0.55+       0.38 - 0.52
Mean Cell Volume      fL          99+         80 - 98
Mean Cell Haemoglobin pg          36+         27 - 35
Platelet Count        x10*9/L     444         150 - 450
White Cell Count      x10*9/L     4.6         4.0 - 11.0
Neutrophils           %           20
Neutrophils           x10*9/L     0.9---      2.0 - 7.5
Lymphocytes           %           20
Lymphocytes           x10*9/L     0.9-        1.1 - 4.0
Monocytes             %           20
Monocytes             x10*9/L     0.9         0.2 - 1.0
Eosinophils           %           20
Eosinophils           x10*9/L     0.92++      0.04 - 0.40
Basophils             %           20
Basophils             x10*9/L     0.92+++     <0.21
```

Below is an example of a panel from `labresults.csv`:

```
40681648,41860BONALP~55,09/08/2014,BONE PROFILE,BON,ALP~55,ALB~37,CA~2.18,xCCA~2.34,PHOS~1.29,,,,,,,,,,,,,,,,,,,,,ALP,IU/L,35,104
40681648,41860BONALP~55,09/08/2014,BONE PROFILE,BON,ALP~55,ALB~37,CA~2.18,xCCA~2.34,PHOS~1.29,,,,,,,,,,,,,,,,,,,,,ALB,g/L,34,50
40681648,41860BONALP~55,09/08/2014,BONE PROFILE,BON,ALP~55,ALB~37,CA~2.18,xCCA~2.34,PHOS~1.29,,,,,,,,,,,,,,,,,,,,,CA,mmol/L,2.2,2.6
40681648,41860BONALP~55,09/08/2014,BONE PROFILE,BON,ALP~55,ALB~37,CA~2.18,xCCA~2.34,PHOS~1.29,,,,,,,,,,,,,,,,,,,,,xCCA,mmol/L,2.2,2.6
40681648,41860BONALP~55,09/08/2014,BONE PROFILE,BON,ALP~55,ALB~37,CA~2.18,xCCA~2.34,PHOS~1.29,,,,,,,,,,,,,,,,,,,,,PHOS,mmol/L,0.87,1.45
```

*Note:* the `sample id` is not unique, for example `41860BONALP~55` may be repeated several times, as the same specimen was used for repeated tests.

This represents the following lab results:

```
Code   Value  Units    Lower  Upper
-----------------------------------
ALP    55     IU/L     35     104
ALB    37     g/L      34     50
CA     2.18   mmol/L   2.2    2.6
xCCA   2.34   mmol/L   2.2    2.6
PHOS   1.29   mmol/L   0.87   1.45
```

The file `labresults-codes.csv` will help you convert the lab's codes into simple SNOMED CT codes, for example:

A code of `HBGL` corresponds to a SNOMED code of `718-7` and indicates the Haemoglobin count in the blood sample.

A 'panel' is a series of tests performed together on a group of samples.

#### Requirements

- use any language you like
- parse the files in any way you think is best
- pretty print the output JSON file and call it `output.json`
- include both your code and the output file in your answer

It would be great if we could build and run all of your code in one go for all the answers to all of your questions.

# And finally...

Good luck and have fun!