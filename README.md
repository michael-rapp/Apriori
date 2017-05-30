# Apriori - README

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=X75YSLEJV3DWE)

This is a Java library, which provides an implementation of the Apriori algorithm [1]. It can be used to efficiently find frequent item sets in large data sets and (optionally) allows to generate association rules. A famous use-case of the Apriori algorithm is to create recommendations of relevant articles in online shops by learning association rules from the purchases customers made in the past. The library provides the following features:

* Searching for frequent item sets, which fulfill a certain minimum support
* Trying to find a specific number of frequent items sets by starting with a great minimum support and iteratively decreasing it until enough item sets are found
* Generating association rules, which reach a minimum confidence, from frequent item sets
* Trying to generate a specific number of association rules by starting with a great minimum confidence and decreasing it until enough rules can be generated
* Association rules can be sorted by their support, confidence, lift or leverage
* Association rules can be filtered to ensure that they reach a certain support, confidence, lift or leverage

## License Agreement

This project is distributed under the Apache License version 2.0. For further information about this license agreement's content please refer to its full version, which is available at http://www.apache.org/licenses/LICENSE-2.0.txt.

## Download

The latest release of this library can be downloaded as a zip archive from the download section of the project's Github page, which is available [here](https://github.com/michael-rapp/Apriori/releases). Furthermore, the library's source code is available as a Git repository, which can be cloned using the URL https://github.com/michael-rapp/Apriori.git.

Alternatively, the library can be added to your project as a Gradle dependency by adding the following dependency to the `build.gradle` file:

```groovy
dependencies {
    compile 'com.github.michael-rapp:apriori:1.0.0'
}
```

## Preliminaries

To be able to apply the Apriori algorithm to a data set, the data must be available in the following form:

* The data set must consist of a finite set of "items" (e.g. the articles available in an online shop). The library provides the interface `Item`, which must be implemented in order to specify the properties of an item. It is important to correctly override the `hashCode`- and `equals`-methods and to implement the `compareTo`-method of the interface `java.lang.Comparable`.
* A data set consists of multiple "transactions" (e.g. the purchases of individual customers). Each transaction contains one or several items. The library provides the interface `Transaction`, which must be implemented to provide a `java.util.Ìterator`, which allows to traverse the items of a transaction.
* To execute the Apriori algorithm, a `java.util.Iterator`, which allows to traverse all available transactions, must be passed to the libary's `Apriori` class.

An exemplary implementation of the interface `Item` can be found in the class `NamedItem`, which is part of the library's JUnit tests. It implements an item, which consists of a text. Furthermore, the library's test resources include the class `DataIterator`, which implements a `java.util.iterator`, which reads items from text files and provides them as transactions. In practice, by creating custom iterator and transaction implementations, the library can flexibly be used to process data from various data sources, such as databases, files, or network resources.

## Finding Frequent Item Sets

The search for frequent item sets aims at finding items, which often occur collectively in a transaction. The probability of an item set to be part of a transaction is measured by the "support" metric. It calculates as the fraction of transactions, all of the item set's items occur in.

### Example

In order to illustrate the operation of the Apriori algorithm, in the table below an exemplary data set, where each column corresponds to an item and each row denotes a transaction, is given.

|            | bread | butter | coffee | milk | sugar |
| ---------- |:-----:|:------:|:------:|:----:|:-----:|
| customer 1 | 1     | 1      | 0      | 0    | 1     |
| customer 2 | 0     | 0      | 1      | 1    | 1     |
| customer 3 | 1     | 0      | 1      | 1    | 1     |
| customer 4 | 0     | 0      | 1      | 1    | 0     |

The algorithm for finding frequent item sets starts by creating candidates C1 with length 1. For each item set in C1 the support is calculated. If a certain minimum support (e.g. 0.5) is not reached, the respective item set is removed. This results in a set S1, which contains those item sets that are considered frequent.

```
C1 = {{bread}, {butter}, {coffee}, {milk}, {sugar}}
S1 = {{bread}, {coffee}, {milk}, {sugar}}
```

The Apriori algorithm then tries to find additional frequent item sets with a greater length. Therefore new item sets with length k + 1 are created by combining two item sets with length k out of S1. Two item sets are only combined, if the first k - 1 items of both item sets are equal. Because the items, which are contained by item sets, are sorted, this enables to efficiently generate all possible candidates, without generating any duplicates. Among all candidates in C2 those, which reach the minimum support, are chosen, resulting in S2.

```
C2 = {{bread, coffee}, {bread, milk}, {bread, sugar}, {coffee, milk}, {coffee, sugar}, {milk, sugar}}
S2 = {{bread, sugar}, {coffee, milk}, {coffee, sugar}, {milk, sugar}}
```

The process described in the previous step continues until no more candidates can be created. Because the candidates for each iteration are calculated from the frequent item sets found in the previous step, the search for frequent item sets can be implemented in efficiently. This is based on exploiting the anti-monotonicity property of the support metric. This property states, that an item set can only be frequent, if all of its subsets are frequent as well. The other way around, the supersets of an infrequent item set are guaranteed to also be infrequent. Given S2, the one more candidate for C3 can be created. Because that candidate reaches the minimum support it remains in S3.

```
C3 = {{coffee, milk, sugar}}
S3 = {{coffee, milk, sugar}}
```
As only one frequent item set is contained by S3, no more candidates can be created for the next iteration and hence the algorithm terminates. As its result, the unit of S1, S2 and S3 are returned.

### Searching for frequent item sets using a minimum support

To use the implementation of the Apriori algorithm, which is provided by this library, for finding frequent item sets, an instance of the class `Apriori` must be created. It can be configured by using the builder pattern as shown below. The generic type argument `NamedItem` corresponds to the class used in the library's JUnit tests as mentioned above. The iterator `DataIterator` can be found in the test resources as well. In order to use custom data sources, it must be replaced by an custom implementation. The value `0.5`, which is passed to the builder as a constructor argument, corresponds to the minimum support, which should be used by the algorithm as described in the example above.

```java
double minSupport = 0.5;
Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(minSupport).create();
Iterator<Transaction<NamedItem>> iterator = new DataIterator(inputFile);
Output<NamedItem> output = apriori.execute(iterator);
SortedSet<ItemSet<NamedItem>> frequentItemSets = output.getFrequentItemSets();
```

By invoking the `execute`-method of the class `Apriori` the execution of the algorithm is started. It results in an instance of the class `Output` to be returned. Such an output contains various information about the executed algorithm. By calling the `getFrequentItemSets`-method, the frequent item sets, which have been found by the algorithm, can be obtained. They are given as instances of the class `ItemSet` and are sorted by their support in decreasing order. The class `ItemSet` can be used like a regular `java.util.SortedSet` and additionally provides the `getSupport`-method, which allows to retrieve the support of an item set.

### Trying to find a specific number of frequent item sets

As an alternative to specifying a certain minimum support, the algorithm can be configure to use varying values. This allows to search for a specific number of frequent item sets by starting with a great value for the minimum support and decreasing it successively until the given number of item sets is found or the minimum support reaches 0. In order to configure an instance of the class `Apriori` that way, the following code can be used:

```java
int count = 5;
Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(count).supportDelta(0.1).maxSupport(1.0).minSupport(0.0).create();
// ...
```

The `count` variable, which is passed to the constructor of the builder in the example above, specifies the number of frequent item sets, which should be found. In addition, the `supportDelta`-method call allows to specify the value, the minimum support should successively be decreased by, starting with the value given by the ``maxSupport``-method. If not enough rules can be found until the support, which is specified by calling the ``minSupport``-method,  is reached, the search for frequent item sets is aborted.

## Generating Association Rules

This library also allows to generate association rules from frequent item sets. An association rule consists of a head Y and a body X and is denoted as X -> Y. It specifies, that if the items in the body take part in a transaction, the items in the head occur as well with a certain probability. That probability is measured by using the confidence metric. It measures the proportion of transactions for which the head is true among those transactions for which the body is true.

### Example

In order to illustrate how association rules are generated according to the Apriori algorithm, the frequent item sets, which have been determined in the example above, are used. They are listed in the following:

```
{{bread}, {coffee}, {milk}, {sugar}, {bread, sugar}, {coffee, milk}, {coffee, sugar}, {milk, sugar}, {coffee, milk, sugar}}
```

The search for association rules, which reach a certain minimum confidence, is pruned by exploiting the anti-monotonicity property of the confidence metric. According to said property, the confidence of a rule A,B -> C is an upper bound to the confidence of a rule A -> B,C. Consequently, the algorithm starts by generating rules, which contain a single item in there heads. Based on those rules, which reach the given minimum confidence, additional rules are created by moving items from their bodies to the heads. For each rule said process is continued until the minimum threshold cannot be reached anymore. As an example, it is shown in the following how association rules are generated from the item set `{coffee, milk, sugar}` when using a minimum confidence of 1.0:

1. Three rules with a single item in their head can be created: `{coffee, milk} - > {sugar}`, `{sugar, coffee} -> {milk}` and `{milk, sugar} -> {coffee}`
2. The last two rules reach the minimum confidence of 1.0 and therefore the algorithm tries to create new rules from them by moving each of the items in the body to the head. In case of the last rule, this result in the rules `{milk} -> {sugar, coffee}` and `{sugar} -> {milk, coffee}` to be created.
3. As the confidence of both rules, which have been created in step 2, is less than 1.0, they are not taken into account and no more rules are derived from them

After testing all candidate rules, the algorithm results in the following rule set being learned:

```
{bread} -> {sugar}
{milk -> {coffee}
{coffee -> {milk}
{milk, sugar} -> {coffee}
{sugar, coffee} -> {milk}
```

### Generating all association rules with a minimum confidence

In order to configure the algorithm, which is provided by this library, to generate association rules, the builder pattern must be used as follows. As the generation of association rules requires to identify frequent item sets beforehand, the builder must be configured as shown above at first. In addition, the builder's `generateRules`-method can be used to specify, that association rules, which reach a certain minimum confidence, should be generated.

```java
double minSupport = 0.5;
double minConfidence = 1.0;
Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(minSupport).generateRules(minConfidence).create();
Iterator<Transaction<NamedItem>> iterator = new DataIterator(inputFile);
Output<NamedItem> output = apriori.execute(iterator);
RuleSet<NamedItem> ruleSet = output.getRuleSet();
```

The induced rules can be obtained from the resulting `Output` instance by calling the `getRuleSet`-method. They are contained by an instance of the class `RuleSet`, which implements the interface `java.util.SortedSet`. By default, the rules of a rule set are sorted by their confidence in decreasing order. The individual rules are given as instances of the class `AssociationRule`. It provides `getBody`- and `getHead`-methods, which return the rule's body and head as `ItemSet` instances.

### Trying to generate a specific number of association rules

In addition to generating all rules, which reach a certain minimum confidence, the algorithm can be configured to try to generate a specific number of rules. The sample code below illustrates how a `Apriori` instance can be created to meet this requirement:

```java
double minSupport = 0.5;
int ruleCount = 5;
Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(minSupport).generateRules(ruleCount).confidenceDelta(0.1).maxConfidence(1.0).minConfidence(0.0).create();
// ...
```

When configuring the builder that way, the semantics of the `confidenceDelta`-, `minConfidence`- and `maxConfidence`-method calls are similar to those of the `supportDelta`-, `maxSupport`- and `minSupport`-method calls discussed above.

### Measuring the "interestingly" of association rules

The Apriori algorithm usually results in many association rules being learned. This requires to filter the rules by "interestingly". However, it is not easy to determine, whether a rule is interesting, or not. To measure the interestingly of rules additional metrics beside support and confidence - namely lift and leverage - can be used. They are defined as follows:

* **Lift:** The ratio of a rule's confidence over a priori expectation for the head.
* **Leverage:** The difference between support and expected support, if the rule's body and head were independent.

Leverage is a lower bound for support and high leverage implies, that the support is also high. In contrast to optimizing the confidence or lift of a rule, optimizing the leverage guarantees, that a certain minimum support is reached.

To measure the support, confidence, lift or leverage of an `AssociationRule`, the following code can be used.

```java
double support = new Support().evaluate(rule); // between 0 and 1
double confidence = new Confidence().evaluate(rule); // between 0 and 1
double lift = new Lift().evaluate(rule); // may be greater than 1
double leverage = new Leverage().evaluate(rule); // between 0 and 1
```

### Sorting and filtering association rules

## Logging

The library uses the [SLF4J](https://www.slf4j.org/) logging facade for writing log messages at different granularities. By adding a logging framework such as [Log4J](https://logging.apache.org/log4j/) or [Logback](https://logback.qos.ch/) to your project and creating a respective configuration files, the Apriori algorithm's log messages can be written to different outputs such as files, databases or the console.

## Contact information

For personal feedback or questions feel free to contact me via the mail address, which is mentioned on my [Github profile](https://github.com/michael-rapp). If you have found any bugs or want to post a feature request please use the [bugtracker](https://github.com/michael-rapp/Apriori/issues) to report them.

## References

[1] Rakesh Agrawal and Ramakrishnan Srikant [Fast algorithms for mining association rules in large databases](http://rakesh.agrawal-family.com/papers/vldb94apriori.pdf). Proceedings of the 20th International Conference on Very Large Data Bases, VLDB, pages 487-499, Santiago, Chile, September 1994.