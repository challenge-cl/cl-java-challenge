# Castlabs Java Coding Challenge

Solution for the assignment from Castlabs for creating a Spring Boot application with a single controller that accepts
a HTTP Get request with a URL set as parameter. The application should retrieve the file and perform an analysis of the
mp4 boxes contained by the file, namely the box types and sizes and return a machine-readable representation of the 
boxes.

The following assumptions where made for this exercise:

- The box types MOOF and TRAF only contain other boxes.
- All other boxes contain payload and do not contain other boxes.

## Solution

I have created a controller Mp4AnalyzerController which handles the HTTP Get request for the path /mp4/analyze with a
URL query string parameter. For this exercise, I have not handled unhappy paths such as invalid URLs or URLs which do
not contain a valid MP4 file. In a real world application, some validations would need to be in place to guarantee 
these constraints.

The controller is simply calling the analyzeMp4 method from the Mp4AnalyzerService which is responsible for downloading
the file from the URL and parsing the MP4 boxes according to the specifications of this exercise. Again in a real world
exercise, we would need to parse a lot more boxes in order to support the full spec.

The controller returns a Mono which generates a finite Mono stream from the value produced by the service which in this
case is a ISOBmff object containing the tree representation of the Mp4 boxes. The value is then translated to the JSON
representation when returned to the client.

I have created tests for both the service and the controller to guarantee that the application is working as expected
with the sample file.

### Running the application

The application can be started with the maven task spring-boot:run:

```bash
./mvnw spring-boot:run 
```

This should launch the application running on port 8080.

In order to test the application, we can use curl to instruct the application to process the sample file:

```bash
curl -s http://localhost:8080/mp4/analyze\?url\=https://demo.castlabs.com/tmp/text0.mp4 
```

We can use jq to help with the output formating:

```bash
curl -s http://localhost:8080/mp4/analyze\?url\=https://demo.castlabs.com/tmp/text0.mp4 | jq '.'
{
  "boxes": [
    {
      "box": {
        "type": "moof",
        "length": 181
      },
      "childs": [
        {
          "box": {
            "type": "mfhd",
            "length": 16
          }
        },
        {
          "box": {
            "type": "traf",
            "length": 157
          },
          "childs": [
            {
              "box": {
                "type": "tfhd",
                "length": 24
              }
            },
            {
              "box": {
                "type": "trun",
                "length": 20
              }
            },
            {
              "box": {
                "type": "uuid",
                "length": 44
              }
            },
            {
              "box": {
                "type": "uuid",
                "length": 61
              }
            }
          ]
        }
      ]
    },
    {
      "box": {
        "type": "mdat",
        "length": 17908
      }
    }
  ]
}

```

