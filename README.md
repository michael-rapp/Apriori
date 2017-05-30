# Apriori - README

This is a Java library, which provides an implementation of the Apriori algorithm [1]. It can be used to efficiently find frequent item sets in large data sets and (optionally) allows to generate association rules. A famous use-case of the Apriori algorithm is to create recommendations of relevant articles in online shops by learning association rules from the purchases customers made in the past.

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

To use the implementation of the Apriori algorithm, which is provided by this library, for finding frequent item sets, an instance of the class `Apriori` must be created. It can be configured by using the builder pattern as shown below. The generic type argument `NamedItem` corresponds to the class used in the library's JUnit tests as mentioned above. The iterator `DataIterator` can be found in the test resources as well. In order to use custom data sources, it must be replaced by an custom implementation. The value `0.5`, which is passed to the builder as a constructor argument, corresponds to the minimum support, which should be used by the algorithm as described in the example above. By invoking the `execute`-method the execution of the algorithm is started. It results in an instance of the class `Output` to be returned. Such an output contains various information about the executed algorithm. By calling the `getFrequentItemSets`-method, the frequent item sets, which have been found by the algorithm, can be obtained. They are sorted by their support in decreasing order.

```java
double minSupport = 0.5;
Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(minSupport).create();
Iterator<Transaction<NamedItem>> iterator = new DataIterator(inputFile);
Output<NamedItem> output = apriori.execute(iterator);
Set<ItemSet<NamedItem>> frequentItemSets = output.getFrequentItemSets();
```

### Trying to find a specific number of frequent item sets

As an alternative to specifying a certain minimum support, the algorithm can be configure to use varying values. This allows to search for a specific number of frequent item sets by starting with a great value for the minimum support and decreasing it successively until the given number of item sets is found or the minimum support reaches 0. In order to configure an instance of the class `Apriori` that way, the following code can be used:

```java
int count = 5;
Apriori<NamedItem> apriori = new Apriori.Builder<NamedItem>(count).supportDelta(0.1).maxSupport(1.0).minSupport(0.0).create();
```

The `count` variable, which is passed to the constructor of the builder in the example above, specifies the number of frequent item sets, which should be found. In addition, the `supportDelta`-method call allows to specify the value, the minimum support should successively be decreased by, starting with the value given by the ``maxSupport``-method. If not enough rules can be found until the support, which is specified by calling the ``minSupport``-method,  is reached, the search for frequent item sets is aborted.

## Generating Association Rules

## References

[1] Rakesh Agrawal and Ramakrishnan Srikant [Fast algorithms for mining association rules in large databases](http://rakesh.agrawal-family.com/papers/vldb94apriori.pdf). Proceedings of the 20th International Conference on Very Large Data Bases, VLDB, pages 487-499, Santiago, Chile, September 1994.