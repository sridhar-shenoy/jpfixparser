# FIX MESSAGE PARSER #

## What was the requirement? ###
```

Your task is to write a FIX parser.  
You can assume that the incoming FIX message will be in a byte array (byte[] msg) 
and the given byte array will contain one and only one whole FIX message.  

Provide an API for people to use your parser.

Goal:

API must be simple to use
* Design for efficiency: high performance and small memory footprint. 
* Your goal is make your parser as fast as possible.      
* Do some benchmarks and show the results.

Constraints:

* You must not use any third-party libraries.  Exception is that Junit is allowed in your unit tests.
* You have one week to finish the task.By the date you receive the email, you have 7 calendar days to finish the task.
```


# Highlights #
* Fix Message parser that has **Zero garbage** if used with our recommended best practice.
* Fix Message parsing in **O(N)** time complexity and **constant memory** usage after prolonged usage.
* Reading operation in **O(1)** time complexity with no garbage if used with our recommended best practice.
* Supports reading of **multiple occurrences** of the same repeating groups
* Support readonly clone method to store messages within application cache *optimized for memory* 

## Api Offered ##

### Parsing Fix Message
* All users of this api have to use the below parse method that takes in fix message in byte array
> void parse(byte[] msg) throws MalformedFixMessageException

### Reading Fix Tag ( Recommended )
* Users of this api can pass in byte array from a pooled cache to extract tag value
* This method copies the value in the output array & return the length of the string
* if the output array is insufficient to hold the data, api returns -1 and keeps the array **_untouched_**

Simple fix Tag
> int copyByteValuesToArray(int tag, byte[] output);

Repeat group fix Tag
> int copyByteValuesToArray(int tag, int repeatBeginTag, int instance, int instanceInMessage, byte[] output);

### Reading Fix Tag ( With memory footprint )
* These methods return newly created String or byte array with the extracted data.
> String getStringValueForTag(int tag);

> byte[] getByteValueForTag(int tag);

> byte[] getByteValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);

>  String getStringValueForTag(int tag, int repeatBeginTag, int instance, int instanceInMessage);


## Example Code ##
```java
    
  //-- Initialize Parser once for this code path    
  FixMessageParser fixMessageParser = FixMessageParserFactory.getFixMessageParser();

  //-- Any number of fix message can be parsed without any memory footprint
  String clientsFixMessage = "8=FIX.4.2\u00019=178\u000135=D\u000134=4\u000149=CLIENT12\u000152=20130615-19:30:00\u000156=BROKER12\u0001" +
  "453=2\u0001" +
  "448=JPMORGAN\u0001447=5\u0001452=6\u0001" +
  "448=CLIENT2\u0001447=D\u0001452=7\u0001" +
  "55=0001.HK\u0001"+
  "453=1\u0001" +
  "448=BCAN\u0001447=1\u0001452=100\u0001" +
  "10=037\u0001";
  fixMessageParser.parse(clientsFixMessage.getBytes());

  //-- Using String based extraction for clarity
          
  //-- parse simple tags        
  System.out.println(fixMessageParser.getStringValueForTag(8));
  System.out.println(fixMessageParser.getStringValueForTag(35));

  //-- parse repeat groups
  System.out.println(fixMessageParser.getStringValueForTag(448,453,0,0));
```

## Scope of future enhancements
*  Support optimized clone method



