# Castlabs Java Coding Challenge

Solution for the assignment from Castlabs for creating a Spring Boot application with a single controller that accepts
a HTTP Get request with a URL set as parameter. The application should retrieve the file and perform an analysis of the
mp4 boxes contained by the file, namely the box types and sizes and return a machine-readable representation of the 
boxes.

The following assumptions where made for this exercise:

- The box types MOOF and TRAF only contain other boxes.
- All other boxes contain payload and do not contain other boxes.

## Solution

### Running the application