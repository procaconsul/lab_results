# Instructions

For the application build, I have chosen sbt (which I had never used before).
To compile, from the ```lab_result``` directory type ```sbt```.
After the build, type ```run``` at the prompt. The application will execute according to the configuration 
in ```src/main/resources/application.conf```.

# Remarks
I have decided to tackle this task in Scala, despite my limited exposure to the language.
The motivation behind this decision is that the task could be easily destructured in sub-problems that could be reassembled 
together in a functional fashion, and Scala offers exceptionally good functional capabilities.
Scala has also been much fun to me since I got to use it for the first time, less than 
a year ago. 
I have developed the business logic of the application according to few assumptions:
- The shape of the provided data is correct;
- There are no special cases beyond those present in the dataset provided.

Clearly this assumptions are reasonable for the limited scope of this task. 
I would not dare to assume as much in a more open-ended application.
