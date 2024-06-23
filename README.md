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
* On windows this parser displayed a performance of **1.24 micro secs** per message and uses approximately **50 Kb** memory with default settings
* On windows it should consume approximately **800,000** messages per sec.  
* This parser has **Zero garbage**, if used with our recommended best practice. ( benchmark results below )
* Parsing has a time complexity of **O(N)** and space complexity of **O(1)**.  
* Parser memory usage remains **Constant** for the entire lifecycle.
* Reading operation has a time complexity of **O(1)**  and with no garbage if used with our recommended best practice.
* Supports reading of **multiple occurrences** of the same repeating groups
* Support readonly clone method to store messages within application cache *optimized for memory* 

### Approach ###
* Keep all internal state as fixed length arrays and only initialized at startup. This achieves **O(1)** space complexity
* Use Only one single loop to parse the Fix Message hence achieving **O(N)** time complexity
* **No new objects are created** during lifecycle to achieve **Zero Garbage** target
* All Lookups must be array based to achieve **O(1)** read operation
* Implemented this using **TDD** approach

 
# BenchMark
> Please refer to  https://github.com/sridhar-shenoy/jpfixparser/blob/master/src/test/java/com/jpm/benchmark/ParsingPerformanceTest.java

#### How do you read this report?
* `HighPerformanceLowMemoryFixParser` Is our offering that parses with zero garbage footprint
* `TestOnlyParser` is a typical string based parser using java collections and strings
* In each of these row sample fix message is passed to parse method `iteration` amount of time
* For each of these executions time and memory footprint is recorded.

#### Machine
> Intel64 Family 6 Model 165 Stepping 5 GenuineIntel ~800 Mhz

### RESULT #1 - Performance with non-realistic and long fix message
#### Fix Message used
* Sample fix message at the bottom of this page
* This message is an unrealistic Fix message just to gauge worst case performance

#### Report analysis
* Table shows 
  * `HighPerformanceLowMemoryFixParser` having 0 Memory footprint and parsing in about 23 micro sec
  * `TestOnlyParser` on the other hand has a lot of garbage and triggered full GC during operations 
```
                         Class Name  |  Iteration |       Total Time taken  |                Average Time per message |       Memory Usage | Total Memory
   HighPerformanceLowMemoryFixParser |      50000 |                 1235 ms |                          24.70 Micro/msg|            0.00 Kb |     14.15 Gb 
   HighPerformanceLowMemoryFixParser |      80000 |                 1915 ms |                          23.94 Micro/msg|            0.00 Kb |     14.15 Gb 
   HighPerformanceLowMemoryFixParser |     100000 |                 2329 ms |                          23.29 Micro/msg|            0.00 Kb |     14.15 Gb 
   HighPerformanceLowMemoryFixParser |     300000 |                 7053 ms |                          23.51 Micro/msg|            0.00 Kb |     14.15 Gb 
   HighPerformanceLowMemoryFixParser |     500000 |                12151 ms |                          24.30 Micro/msg|            0.00 Kb |     14.15 Gb 
   HighPerformanceLowMemoryFixParser |     800000 |                19844 ms |                          24.81 Micro/msg|            0.00 Kb |     14.15 Gb 
   HighPerformanceLowMemoryFixParser |    1000000 |                30955 ms |                          30.96 Micro/msg|            0.00 Kb |     14.15 Gb 
                      TestOnlyParser |      50000 |                30741 ms |                         614.82 Micro/msg|      2203048.79 Kb |     14.15 Gb 
                      TestOnlyParser |      80000 |                13533 ms |                         169.16 Micro/msg|       443832.70 Kb |     14.15 Gb 
                      TestOnlyParser |     100000 |                15636 ms |                         156.36 Micro/msg|     -1816010.76 Kb |     14.15 Gb 
                      TestOnlyParser |     300000 |                48460 ms |                         161.53 Micro/msg|       877478.74 Kb |     14.15 Gb 
                      TestOnlyParser |     500000 |                82031 ms |                         164.06 Micro/msg|       375985.30 Kb |     14.15 Gb 
                      TestOnlyParser |     800000 |               130958 ms |                         163.70 Micro/msg|     -1942783.60 Kb |     14.15 Gb 
                      TestOnlyParser |    1000000 |               191495 ms |                         191.50 Micro/msg|       752001.27 Kb |     14.15 Gb 

```
### RESULT #2 - Production like fix message  
#### Fix Message used
> 8=FIX.4.49=19335=AD49=bthomas_trading_brokers_llc56=CMESTPFIX234=757=STP52=20160330-21:23:01.588779=20160330-20:50:48.800568=d0f298f7-a285-4cd8-9207-9eab0630582a569=1263=1453=1448=ace452=7442=210=099

##### Source
> https://cmegroupclientsite.atlassian.net/wiki/spaces/EPICSANDBOX/pages/46472218/CME+STP+FIX+-+Samples+for+BrokerTec+Trades

#### Report analysis
* Table shows
    * `HighPerformanceLowMemoryFixParser` having 0 Memory footprint and parsing in about 1.24 micro sec
    * `TestOnlyParser` on the other hand has a lot of garbage during operations
```
                         Class Name  |  Iteration |       Total Time taken  |                Average Time per message |       Memory Usage | Total Memory
   HighPerformanceLowMemoryFixParser |      50000 |                   73 ms |                           1.46 Micro/msg|            0.00 Kb |      0.30 Gb 
   HighPerformanceLowMemoryFixParser |      80000 |                   99 ms |                           1.24 Micro/msg|            0.00 Kb |      0.30 Gb 
   HighPerformanceLowMemoryFixParser |     100000 |                  142 ms |                           1.42 Micro/msg|            0.00 Kb |      0.30 Gb 
   HighPerformanceLowMemoryFixParser |     300000 |                  374 ms |                           1.25 Micro/msg|            0.00 Kb |      0.30 Gb 
   HighPerformanceLowMemoryFixParser |     500000 |                  627 ms |                           1.25 Micro/msg|            0.00 Kb |      0.30 Gb 
   HighPerformanceLowMemoryFixParser |     800000 |                 1004 ms |                           1.26 Micro/msg|            0.00 Kb |      0.30 Gb 
   HighPerformanceLowMemoryFixParser |    1000000 |                 1228 ms |                           1.23 Micro/msg|            0.00 Kb |      0.30 Gb 
                      TestOnlyParser |      50000 |                  160 ms |                           3.20 Micro/msg|        88545.27 Kb |      0.30 Gb 
                      TestOnlyParser |      80000 |                  225 ms |                           2.81 Micro/msg|       167147.34 Kb |      0.42 Gb 
                      TestOnlyParser |     100000 |                  322 ms |                           3.22 Micro/msg|       -73003.43 Kb |      0.66 Gb 
                      TestOnlyParser |     300000 |                  819 ms |                           2.73 Micro/msg|       536335.79 Kb |      1.16 Gb 
                      TestOnlyParser |     500000 |                 1118 ms |                           2.24 Micro/msg|      -363544.04 Kb |      0.98 Gb 
                      TestOnlyParser |     800000 |                 1768 ms |                           2.21 Micro/msg|      -275590.72 Kb |      0.73 Gb 
                      TestOnlyParser |    1000000 |                 2232 ms |                           2.23 Micro/msg|        62269.09 Kb |      0.66 Gb 
```
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
* Support Nested Repeating group
* Enhance clone to further optimize on memory footprint
* Support add/delete tag



#### Unrealistic Fix Message used for Performance
> > 1=8QlrDD9q2=8QlrDD9q3=8QlrDD9q4=8QlrDD9q5=8QlrDD9q6=8QlrDD9q7=8QlrDD9q8=8QlrDD9q9=8QlrDD9q10=8QlrDD9q11=8QlrDD9q12=8QlrDD9q13=8QlrDD9q14=8QlrDD9q15=8QlrDD9q16=8QlrDD9q17=8QlrDD9q18=8QlrDD9q19=8QlrDD9q20=8QlrDD9q21=8QlrDD9q22=8QlrDD9q23=8QlrDD9q24=8QlrDD9q25=8QlrDD9q26=8QlrDD9q27=8QlrDD9q28=8QlrDD9q29=8QlrDD9q30=8QlrDD9q31=8QlrDD9q32=8QlrDD9q33=8QlrDD9q34=8QlrDD9q35=8QlrDD9q36=8QlrDD9q37=8QlrDD9q38=8QlrDD9q39=8QlrDD9q40=8QlrDD9q41=8QlrDD9q42=8QlrDD9q43=8QlrDD9q44=8QlrDD9q45=8QlrDD9q46=8QlrDD9q47=8QlrDD9q48=8QlrDD9q49=8QlrDD9q50=8QlrDD9q51=8QlrDD9q52=8QlrDD9q53=8QlrDD9q54=8QlrDD9q55=8QlrDD9q56=8QlrDD9q57=8QlrDD9q58=8QlrDD9q59=8QlrDD9q60=8QlrDD9q61=8QlrDD9q62=8QlrDD9q63=8QlrDD9q64=8QlrDD9q65=8QlrDD9q66=8QlrDD9q67=8QlrDD9q68=8QlrDD9q69=8QlrDD9q70=8QlrDD9q71=8QlrDD9q72=8QlrDD9q73=8QlrDD9q74=8QlrDD9q75=8QlrDD9q76=8QlrDD9q77=8QlrDD9q78=8QlrDD9q79=8QlrDD9q80=8QlrDD9q81=8QlrDD9q82=8QlrDD9q83=8QlrDD9q84=8QlrDD9q85=8QlrDD9q86=8QlrDD9q87=8QlrDD9q88=8QlrDD9q89=8QlrDD9q90=8QlrDD9q91=8QlrDD9q92=8QlrDD9q93=8QlrDD9q94=8QlrDD9q95=8QlrDD9q96=8QlrDD9q97=8QlrDD9q98=8QlrDD9q99=8QlrDD9q100=8QlrDD9q101=8QlrDD9q102=8QlrDD9q103=8QlrDD9q104=8QlrDD9q105=8QlrDD9q106=8QlrDD9q107=8QlrDD9q108=8QlrDD9q109=8QlrDD9q110=8QlrDD9q111=8QlrDD9q112=8QlrDD9q113=8QlrDD9q114=8QlrDD9q115=8QlrDD9q116=8QlrDD9q117=8QlrDD9q118=8QlrDD9q119=8QlrDD9q120=8QlrDD9q121=8QlrDD9q122=8QlrDD9q123=8QlrDD9q124=8QlrDD9q125=8QlrDD9q126=8QlrDD9q127=8QlrDD9q128=8QlrDD9q129=8QlrDD9q130=8QlrDD9q131=8QlrDD9q132=8QlrDD9q133=8QlrDD9q134=8QlrDD9q135=8QlrDD9q136=8QlrDD9q137=8QlrDD9q138=8QlrDD9q139=8QlrDD9q140=8QlrDD9q141=8QlrDD9q142=8QlrDD9q143=8QlrDD9q144=8QlrDD9q145=8QlrDD9q146=8QlrDD9q147=8QlrDD9q148=8QlrDD9q149=8QlrDD9q150=8QlrDD9q151=8QlrDD9q152=8QlrDD9q153=8QlrDD9q154=8QlrDD9q155=8QlrDD9q156=8QlrDD9q157=8QlrDD9q158=8QlrDD9q159=8QlrDD9q160=8QlrDD9q161=8QlrDD9q162=8QlrDD9q163=8QlrDD9q164=8QlrDD9q165=8QlrDD9q166=8QlrDD9q167=8QlrDD9q168=8QlrDD9q169=8QlrDD9q170=8QlrDD9q171=8QlrDD9q172=8QlrDD9q173=8QlrDD9q174=8QlrDD9q175=8QlrDD9q176=8QlrDD9q177=8QlrDD9q178=8QlrDD9q179=8QlrDD9q180=8QlrDD9q181=8QlrDD9q182=8QlrDD9q183=8QlrDD9q184=8QlrDD9q185=8QlrDD9q186=8QlrDD9q187=8QlrDD9q188=8QlrDD9q189=8QlrDD9q190=8QlrDD9q191=8QlrDD9q192=8QlrDD9q193=8QlrDD9q194=8QlrDD9q195=8QlrDD9q196=8QlrDD9q197=8QlrDD9q198=8QlrDD9q199=8QlrDD9q200=8QlrDD9q201=8QlrDD9q202=8QlrDD9q203=8QlrDD9q204=8QlrDD9q205=8QlrDD9q206=8QlrDD9q207=8QlrDD9q208=8QlrDD9q209=8QlrDD9q210=8QlrDD9q211=8QlrDD9q212=8QlrDD9q213=8QlrDD9q214=8QlrDD9q215=8QlrDD9q216=8QlrDD9q217=8QlrDD9q218=8QlrDD9q219=8QlrDD9q220=8QlrDD9q221=8QlrDD9q222=8QlrDD9q223=8QlrDD9q224=8QlrDD9q225=8QlrDD9q226=8QlrDD9q227=8QlrDD9q228=8QlrDD9q229=8QlrDD9q230=8QlrDD9q231=8QlrDD9q232=8QlrDD9q233=8QlrDD9q234=8QlrDD9q235=8QlrDD9q236=8QlrDD9q237=8QlrDD9q238=8QlrDD9q239=8QlrDD9q240=8QlrDD9q241=8QlrDD9q242=8QlrDD9q243=8QlrDD9q244=8QlrDD9q245=8QlrDD9q246=8QlrDD9q247=8QlrDD9q248=8QlrDD9q249=8QlrDD9q250=8QlrDD9q251=8QlrDD9q252=8QlrDD9q253=8QlrDD9q254=8QlrDD9q255=8QlrDD9q256=8QlrDD9q257=8QlrDD9q258=8QlrDD9q259=8QlrDD9q260=8QlrDD9q261=8QlrDD9q262=8QlrDD9q263=8QlrDD9q264=8QlrDD9q265=8QlrDD9q266=8QlrDD9q267=8QlrDD9q268=8QlrDD9q269=8QlrDD9q270=8QlrDD9q271=8QlrDD9q272=8QlrDD9q273=8QlrDD9q274=8QlrDD9q275=8QlrDD9q276=8QlrDD9q277=8QlrDD9q278=8QlrDD9q279=8QlrDD9q280=8QlrDD9q281=8QlrDD9q282=8QlrDD9q283=8QlrDD9q284=8QlrDD9q285=8QlrDD9q286=8QlrDD9q287=8QlrDD9q288=8QlrDD9q289=8QlrDD9q290=8QlrDD9q291=8QlrDD9q292=8QlrDD9q293=8QlrDD9q294=8QlrDD9q295=8QlrDD9q296=8QlrDD9q297=8QlrDD9q298=8QlrDD9q299=8QlrDD9q300=8QlrDD9q301=8QlrDD9q302=8QlrDD9q303=8QlrDD9q304=8QlrDD9q305=8QlrDD9q306=8QlrDD9q307=8QlrDD9q308=8QlrDD9q309=8QlrDD9q310=8QlrDD9q311=8QlrDD9q312=8QlrDD9q313=8QlrDD9q314=8QlrDD9q315=8QlrDD9q316=8QlrDD9q317=8QlrDD9q318=8QlrDD9q319=8QlrDD9q320=8QlrDD9q321=8QlrDD9q322=8QlrDD9q323=8QlrDD9q324=8QlrDD9q325=8QlrDD9q326=8QlrDD9q327=8QlrDD9q328=8QlrDD9q329=8QlrDD9q330=8QlrDD9q331=8QlrDD9q332=8QlrDD9q333=8QlrDD9q334=8QlrDD9q335=8QlrDD9q336=8QlrDD9q337=8QlrDD9q338=8QlrDD9q339=8QlrDD9q340=8QlrDD9q341=8QlrDD9q342=8QlrDD9q343=8QlrDD9q344=8QlrDD9q345=8QlrDD9q346=8QlrDD9q347=8QlrDD9q348=8QlrDD9q349=8QlrDD9q350=8QlrDD9q351=8QlrDD9q352=8QlrDD9q353=8QlrDD9q354=8QlrDD9q355=8QlrDD9q356=8QlrDD9q357=8QlrDD9q358=8QlrDD9q359=8QlrDD9q360=8QlrDD9q361=8QlrDD9q362=8QlrDD9q363=8QlrDD9q364=8QlrDD9q365=8QlrDD9q366=8QlrDD9q367=8QlrDD9q368=8QlrDD9q369=8QlrDD9q370=8QlrDD9q371=8QlrDD9q372=8QlrDD9q373=8QlrDD9q374=8QlrDD9q375=8QlrDD9q376=8QlrDD9q377=8QlrDD9q378=8QlrDD9q379=8QlrDD9q380=8QlrDD9q381=8QlrDD9q382=8QlrDD9q383=8QlrDD9q384=8QlrDD9q385=8QlrDD9q386=8QlrDD9q387=8QlrDD9q388=8QlrDD9q389=8QlrDD9q390=8QlrDD9q391=8QlrDD9q392=8QlrDD9q393=8QlrDD9q394=8QlrDD9q395=8QlrDD9q396=8QlrDD9q397=8QlrDD9q398=8QlrDD9q399=8QlrDD9q400=8QlrDD9q401=8QlrDD9q402=8QlrDD9q403=8QlrDD9q404=8QlrDD9q405=8QlrDD9q406=8QlrDD9q407=8QlrDD9q408=8QlrDD9q409=8QlrDD9q410=8QlrDD9q411=8QlrDD9q412=8QlrDD9q413=8QlrDD9q414=8QlrDD9q415=8QlrDD9q416=8QlrDD9q417=8QlrDD9q418=8QlrDD9q419=8QlrDD9q420=8QlrDD9q421=8QlrDD9q422=8QlrDD9q423=8QlrDD9q424=8QlrDD9q425=8QlrDD9q426=8QlrDD9q427=8QlrDD9q428=8QlrDD9q429=8QlrDD9q430=8QlrDD9q431=8QlrDD9q432=8QlrDD9q433=8QlrDD9q434=8QlrDD9q435=8QlrDD9q436=8QlrDD9q437=8QlrDD9q438=8QlrDD9q439=8QlrDD9q440=8QlrDD9q441=8QlrDD9q442=8QlrDD9q443=8QlrDD9q444=8QlrDD9q445=8QlrDD9q446=8QlrDD9q449=8QlrDD9q450=8QlrDD9q451=8QlrDD9q454=8QlrDD9q455=8QlrDD9q456=8QlrDD9q457=8QlrDD9q458=8QlrDD9q459=8QlrDD9q460=8QlrDD9q461=8QlrDD9q462=8QlrDD9q463=8QlrDD9q464=8QlrDD9q465=8QlrDD9q466=8QlrDD9q467=8QlrDD9q468=8QlrDD9q469=8QlrDD9q470=8QlrDD9q471=8QlrDD9q472=8QlrDD9q473=8QlrDD9q474=8QlrDD9q475=8QlrDD9q476=8QlrDD9q477=8QlrDD9q478=8QlrDD9q479=8QlrDD9q480=8QlrDD9q481=8QlrDD9q482=8QlrDD9q483=8QlrDD9q484=8QlrDD9q485=8QlrDD9q486=8QlrDD9q487=8QlrDD9q488=8QlrDD9q489=8QlrDD9q490=8QlrDD9q491=8QlrDD9q492=8QlrDD9q493=8QlrDD9q494=8QlrDD9q495=8QlrDD9q496=8QlrDD9q497=8QlrDD9q498=8QlrDD9q499=8QlrDD9q500=8QlrDD9q501=8QlrDD9q502=8QlrDD9q503=8QlrDD9q504=8QlrDD9q505=8QlrDD9q506=8QlrDD9q507=8QlrDD9q508=8QlrDD9q509=8QlrDD9q510=8QlrDD9q511=8QlrDD9q512=8QlrDD9q513=8QlrDD9q514=8QlrDD9q515=8QlrDD9q516=8QlrDD9q517=8QlrDD9q518=8QlrDD9q519=8QlrDD9q520=8QlrDD9q521=8QlrDD9q522=8QlrDD9q523=8QlrDD9q524=8QlrDD9q525=8QlrDD9q526=8QlrDD9q527=8QlrDD9q528=8QlrDD9q529=8QlrDD9q530=8QlrDD9q531=8QlrDD9q532=8QlrDD9q533=8QlrDD9q534=8QlrDD9q535=8QlrDD9q536=8QlrDD9q537=8QlrDD9q538=8QlrDD9q539=8QlrDD9q540=8QlrDD9q541=8QlrDD9q542=8QlrDD9q543=8QlrDD9q544=8QlrDD9q545=8QlrDD9q546=8QlrDD9q547=8QlrDD9q548=8QlrDD9q549=8QlrDD9q550=8QlrDD9q551=8QlrDD9q552=8QlrDD9q553=8QlrDD9q554=8QlrDD9q555=8QlrDD9q556=8QlrDD9q557=8QlrDD9q558=8QlrDD9q559=8QlrDD9q560=8QlrDD9q561=8QlrDD9q562=8QlrDD9q563=8QlrDD9q564=8QlrDD9q565=8QlrDD9q566=8QlrDD9q567=8QlrDD9q568=8QlrDD9q569=8QlrDD9q570=8QlrDD9q571=8QlrDD9q572=8QlrDD9q573=8QlrDD9q574=8QlrDD9q575=8QlrDD9q576=8QlrDD9q577=8QlrDD9q578=8QlrDD9q579=8QlrDD9q580=8QlrDD9q581=8QlrDD9q582=8QlrDD9q583=8QlrDD9q584=8QlrDD9q585=8QlrDD9q586=8QlrDD9q587=8QlrDD9q588=8QlrDD9q589=8QlrDD9q590=8QlrDD9q591=8QlrDD9q592=8QlrDD9q593=8QlrDD9q594=8QlrDD9q595=8QlrDD9q596=8QlrDD9q597=8QlrDD9q598=8QlrDD9q599=8QlrDD9q600=8QlrDD9q601=8QlrDD9q602=8QlrDD9q603=8QlrDD9q604=8QlrDD9q605=8QlrDD9q606=8QlrDD9q607=8QlrDD9q608=8QlrDD9q609=8QlrDD9q610=8QlrDD9q611=8QlrDD9q612=8QlrDD9q613=8QlrDD9q614=8QlrDD9q615=8QlrDD9q616=8QlrDD9q617=8QlrDD9q618=8QlrDD9q619=8QlrDD9q620=8QlrDD9q621=8QlrDD9q622=8QlrDD9q623=8QlrDD9q624=8QlrDD9q625=8QlrDD9q626=8QlrDD9q627=8QlrDD9q628=8QlrDD9q629=8QlrDD9q630=8QlrDD9q631=8QlrDD9q632=8QlrDD9q633=8QlrDD9q634=8QlrDD9q635=8QlrDD9q636=8QlrDD9q637=8QlrDD9q638=8QlrDD9q639=8QlrDD9q640=8QlrDD9q641=8QlrDD9q642=8QlrDD9q643=8QlrDD9q644=8QlrDD9q645=8QlrDD9q646=8QlrDD9q647=8QlrDD9q648=8QlrDD9q649=8QlrDD9q650=8QlrDD9q651=8QlrDD9q652=8QlrDD9q653=8QlrDD9q654=8QlrDD9q655=8QlrDD9q656=8QlrDD9q657=8QlrDD9q658=8QlrDD9q659=8QlrDD9q660=8QlrDD9q661=8QlrDD9q662=8QlrDD9q663=8QlrDD9q664=8QlrDD9q665=8QlrDD9q666=8QlrDD9q667=8QlrDD9q668=8QlrDD9q669=8QlrDD9q670=8QlrDD9q671=8QlrDD9q672=8QlrDD9q673=8QlrDD9q674=8QlrDD9q675=8QlrDD9q676=8QlrDD9q677=8QlrDD9q678=8QlrDD9q679=8QlrDD9q680=8QlrDD9q681=8QlrDD9q682=8QlrDD9q683=8QlrDD9q684=8QlrDD9q685=8QlrDD9q686=8QlrDD9q687=8QlrDD9q688=8QlrDD9q689=8QlrDD9q690=8QlrDD9q691=8QlrDD9q692=8QlrDD9q693=8QlrDD9q694=8QlrDD9q695=8QlrDD9q696=8QlrDD9q697=8QlrDD9q698=8QlrDD9q699=8QlrDD9q700=8QlrDD9q701=8QlrDD9q702=8QlrDD9q703=8QlrDD9q704=8QlrDD9q705=8QlrDD9q706=8QlrDD9q707=8QlrDD9q708=8QlrDD9q709=8QlrDD9q710=8QlrDD9q711=8QlrDD9q712=8QlrDD9q713=8QlrDD9q714=8QlrDD9q715=8QlrDD9q716=8QlrDD9q717=8QlrDD9q718=8QlrDD9q719=8QlrDD9q720=8QlrDD9q721=8QlrDD9q722=8QlrDD9q723=8QlrDD9q724=8QlrDD9q725=8QlrDD9q726=8QlrDD9q727=8QlrDD9q728=8QlrDD9q729=8QlrDD9q730=8QlrDD9q731=8QlrDD9q732=8QlrDD9q733=8QlrDD9q734=8QlrDD9q735=8QlrDD9q736=8QlrDD9q737=8QlrDD9q738=8QlrDD9q739=8QlrDD9q740=8QlrDD9q741=8QlrDD9q742=8QlrDD9q743=8QlrDD9q744=8QlrDD9q745=8QlrDD9q746=8QlrDD9q747=8QlrDD9q748=8QlrDD9q749=8QlrDD9q750=8QlrDD9q751=8QlrDD9q752=8QlrDD9q753=8QlrDD9q754=8QlrDD9q755=8QlrDD9q756=8QlrDD9q757=8QlrDD9q758=8QlrDD9q759=8QlrDD9q760=8QlrDD9q761=8QlrDD9q762=8QlrDD9q763=8QlrDD9q764=8QlrDD9q765=8QlrDD9q766=8QlrDD9q767=8QlrDD9q768=8QlrDD9q769=8QlrDD9q770=8QlrDD9q771=8QlrDD9q772=8QlrDD9q773=8QlrDD9q774=8QlrDD9q775=8QlrDD9q776=8QlrDD9q777=8QlrDD9q778=8QlrDD9q779=8QlrDD9q780=8QlrDD9q781=8QlrDD9q782=8QlrDD9q783=8QlrDD9q784=8QlrDD9q785=8QlrDD9q786=8QlrDD9q787=8QlrDD9q788=8QlrDD9q789=8QlrDD9q790=8QlrDD9q791=8QlrDD9q792=8QlrDD9q793=8QlrDD9q794=8QlrDD9q795=8QlrDD9q796=8QlrDD9q797=8QlrDD9q798=8QlrDD9q799=8QlrDD9q800=8QlrDD9q801=8QlrDD9q802=8QlrDD9q803=8QlrDD9q804=8QlrDD9q805=8QlrDD9q806=8QlrDD9q807=8QlrDD9q808=8QlrDD9q809=8QlrDD9q810=8QlrDD9q811=8QlrDD9q812=8QlrDD9q813=8QlrDD9q814=8QlrDD9q815=8QlrDD9q816=8QlrDD9q817=8QlrDD9q818=8QlrDD9q819=8QlrDD9q820=8QlrDD9q821=8QlrDD9q822=8QlrDD9q823=8QlrDD9q824=8QlrDD9q825=8QlrDD9q826=8QlrDD9q827=8QlrDD9q828=8QlrDD9q829=8QlrDD9q830=8QlrDD9q831=8QlrDD9q832=8QlrDD9q833=8QlrDD9q834=8QlrDD9q835=8QlrDD9q836=8QlrDD9q837=8QlrDD9q838=8QlrDD9q839=8QlrDD9q840=8QlrDD9q841=8QlrDD9q842=8QlrDD9q843=8QlrDD9q844=8QlrDD9q845=8QlrDD9q846=8QlrDD9q847=8QlrDD9q848=8QlrDD9q849=8QlrDD9q850=8QlrDD9q851=8QlrDD9q852=8QlrDD9q853=8QlrDD9q854=8QlrDD9q855=8QlrDD9q856=8QlrDD9q857=8QlrDD9q858=8QlrDD9q859=8QlrDD9q860=8QlrDD9q861=8QlrDD9q862=8QlrDD9q863=8QlrDD9q864=8QlrDD9q865=8QlrDD9q866=8QlrDD9q867=8QlrDD9q868=8QlrDD9q869=8QlrDD9q870=8QlrDD9q871=8QlrDD9q872=8QlrDD9q873=8QlrDD9q874=8QlrDD9q875=8QlrDD9q876=8QlrDD9q877=8QlrDD9q878=8QlrDD9q879=8QlrDD9q880=8QlrDD9q881=8QlrDD9q882=8QlrDD9q883=8QlrDD9q884=8QlrDD9q885=8QlrDD9q886=8QlrDD9q887=8QlrDD9q888=8QlrDD9q889=8QlrDD9q890=8QlrDD9q891=8QlrDD9q892=8QlrDD9q893=8QlrDD9q894=8QlrDD9q895=8QlrDD9q896=8QlrDD9q897=8QlrDD9q898=8QlrDD9q899=8QlrDD9q900=8QlrDD9q