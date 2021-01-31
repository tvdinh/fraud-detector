# fraud-detector
Fraud Detection Application

## Content

This repository contains the following files/directories:

1. `fraud-detection.sh`: Executable fraud detection app
2. `pom.xml`: Maven build file
3. `src/`: All java source/test code and resources for development and testing
4. `executable`: Contains the `FraudDetectionApp.jar` that is used in the `fraud-detector.sh`
5. `README.md`: Program documentation.

## Build

### System/Tool requirements

1. Java 8
2. Apache Maven >= 3.6.3
3. Unix Bash

### Step

1. Build the project with maven
```
mvn clean install
```

A final jar file, named `FraudDetectionApp.jar` will be produced in `target/`. A prebuilt version of this jar is also available in `executable/`.

2. Copy to executable directory:

```
cp target/FraudDetectionApp.jar executable/
```

## Run 

```
./fraud-detection.sh <amount limit> <transaction file>
```

If `bash` is not available:

```
java -jar executable/FraudDetectionApp.jar <amount limit> <transaction file>
```

A few examples:

1.
```
./fraud-detection.sh 35.00 src/test/resources/transactions_10_nofraud.csv 
```
or
```
java -jar executable/FraudDetectionApp.jar 35.00 src/test/resources/transactions_10_nofraud.csv 
```

Output:
```
No fraud detected!
```

2.
```
./fraud-detection.sh 35.00 src/test/resources/transactions_10.csv 
```
Output:
```
There are 1 fraudulent credit card(s):
1f409e4283ad6375bf5d4e9372d
```

3.
```
./fraud-detection.sh 35.00 src/test/resources/transactions_15.csv 
```
Output:
```
There are 2 fraudulent credit card(s):
69756add2de732518ffa48974e8
1f409e4283ad6375bf5d4e9372d
```

The app also provides some basic input check:

4.
```
./fraud-detection.sh "Thirty-Five dollars" src/test/resources/transactions_15.csv 
```
Output:
```
Invalid limit [Thirty-Five dollars], expecting numeric value.
Aborting...
```

5.
```
./fraud-detection.sh "35.00" no-exsit
```
Output:
```
File error: no-exsit (No such file or directory).
Aborting...
```

## Design & Solution

### Assumption/clarification

Besides what given in the problem statement (Coding challenge description doco), this session discusses further assumptions or (for some instances) clarifications into some details of this problem. 

1. 24 hour sliding window:

Given a transaction occurs at `2014-04-29T13:15:54`, the past 24 hour sliding window period would be `2014-04-28T13:15:55` to `2014-04-29T13:15:54` inclusive. A transaction of the same credit card occurs at `2014-04-28T13:15:54` (happen 1 second before `2014-04-28T13:15:55`) does not count into the 24 hour sliding window

2. Transaction date time timezone & format.

Assume that all the dates in the transaction record are in the same timezone. And date format is `ISO-8601` without the timezone and to the second precision. E.g
`yyyy-MM-ddTHH:mm::ss`.

3. Credit card/Hash uniqueness

Although collision is possible in theory for hashing, for the scope of this problem, it is assumed that each distinct hashed value represents a distinct credit card number. It means that there wont be a case where two different credit cards result in the same hashed value.

### Solution

The program can be divided into two parts: reading from a file and processing raw data (with validation) of the .csv into a list of sanitized `Transaction` and then performing fraud scan on those transaction against the fraud detection rule. 

In this case, the rule is that a credit card is identified as fraudulent if the sum amount of transactions from that credit card over a 24-hour sliding window period exceeds a given limit.

The first part is taken care of by a `TransactionFileLoader` which is a simple io util class that utilises a `TransactionResolver` to "resolve" or "transform" transaction record text line into a Java POJO `Transaction`. The resolver expects the text line is in the formar `<hashed_credit_card>, <date-time in ISO-8601 no timezone>, <amount>`. If there is an invalid line, the loader simply ignores the line and processes with the next one.

The heart of the program is the `FraudDetector` which takes a list of transactions and performs a fraud scan. The `FraudDetector` scans each transaction (as it comes in chronological order) at a time. At each transaction `T`, it computes the total amount of the past transactions of that credit card and only within the last 24 hours from when `T` occurs. If the total amount exceeds the threshold, the `FraudDetector`  marks that credit card as fraudulent. Once all the transactions have been scanned, the `FraudDetector` returns a list of fraudulent credit cards.

### Limitation & Futher Improvment

This session discusses some of the limitations of this solution. 

The `FraudDetector` as the moment requires the `complete` data of all transactions. So, it is designed to work in `offline` mode where all the transactions are available with a small set of data. Often, the number of transactions in a production cannot be taken as a `List` into a method. Moreover, fraud detection is only useful if it's running in real time, meaning it should be able to detect fraud as the transaction data becomes available (in real time).

All of the above limitations can be overcome with not a great deal of amendment from the current problem. To address the data size problem, since only the transactions of the last 24 hours is of concern, the `FraudDetector` can be updated to keep only transactions within the last 24 hours of the latest observed transaction for a given credit card, thus keeping the data in memory manageable.
As for detecting fraud in real time, we can keep `TransactionFileLoader` and `FraudDetector` run synchronously, meaning the `loader` will feed the `detector` with latest `Transaction` as soon as it becomes available.

## Language and static code analysis

1. Programm is written in Java(8) and Maven is used as the build system.
2. Code is covered by unit tests and integration tests (96% code coverage).

## Testing & Sample

1.
Given the following records

```
10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00
f2f1ee2840d23ed1430cbec83c6, 2014-04-29T13:15:54, 10.00
a89d8b42959392729b9d8e47e8b, 2014-04-29T13:45:54, 10.00
10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-30T15:20:54, 20.00
f2f1ee2840d23ed1430cbec83c6, 2014-04-30T16:15:54, 15.00
1f409e4283ad6375bf5d4e9372d, 2014-04-30T16:25:54, 10.00
1f409e4283ad6375bf5d4e9372d, 2014-05-01T08:15:54, 30.00
f2f1ee2840d23ed1430cbec83c6, 2014-05-02T10:20:54, 8.00
10d7ce2f43e35fa57d1bbf8b1e2, 2014-05-03T13:20:54, 30.00
10d7ce2f43e35fa57d1bbf8b1e2, 2014-05-04T16:20:55, 25.00
```

`Limit: 35.00`

For this case, credit card `1f409e4283ad6375bf5d4e9372d` is a fraud as spend in total 40.00 over 2 consecutive days (2014-04-30, 2014-05-01) , but still within 24 hours. `10d7ce2f43e35fa57d1bbf8b1e2` spends over 35.00 if counting all transactions, but there is no 24 hour period, that it spends over that limit.
```
1f409e4283ad6375bf5d4e9372d, 2014-04-30T16:25:54, 10.00
1f409e4283ad6375bf5d4e9372d, 2014-05-01T08:15:54, 30.00
```

Run
```
./fraud-detection.sh 35.00 src/test/resources/transactions_10.csv 
```
Return
```
There are 1 fraudulent credit card(s):
1f409e4283ad6375bf5d4e9372d
```

