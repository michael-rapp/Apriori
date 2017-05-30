# Apriori - README

This is a Java library, which provides an implementation of the Apriori algorithm [1]. It can be used to efficiently find frequent item sets in large data sets and (optionally) allows to generate association rules. A famous use-case of the Apriori algorithm is to create recommendations of relevant articles in online shops by learning association rules from the purchases customers made in the past.

## Preliminaries

To be able to apply the Apriori algorithm to a data set, the data must be available in the following form:

* The data set must consist of a finite set of "items" (e.g. the articles available in an online shop). The library provides the interface `Item`, which must be implemented in order to specify the properties of an item. It is important to correctly override the `hashCode`- and `equals`-methods and to implement the `compareTo`-method of the interface `java.lang.Comparable`.
* A data set consists of multiple "transactions" (e.g. the purchases of individual customers). Each transaction contains one or several items. The library provides the interface `Transaction`, which must be implemented to provide a `java.util.Ìterator`, which allows to traverse the items of a transaction.
* To execute the Apriori algorithm, a `java.util.Iterator`, which allows to traverse all available transactions, must be passed to the libary's `Apriori` class.

An exemplary implementation of the interface `Item` can be found in the class `NamedItem`, which is part of the library's JUnit tests. It implements an item, which consists of a text. Furthermore, the library's test resources include the class `DataIterator`, which implements a `java.util.iterator`, which reads items from text files and provides them as transactions. In practice, by creating custom iterator and transaction implementations, the library can flexibly be used to process data from various data sources, such as databases, files, or network resources.

## References

[1] Rakesh Agrawal and Ramakrishnan Srikant [Fast algorithms for mining association rules in large databases](http://rakesh.agrawal-family.com/papers/vldb94apriori.pdf). Proceedings of the 20th International Conference on Very Large Data Bases, VLDB, pages 487-499, Santiago, Chile, September 1994.