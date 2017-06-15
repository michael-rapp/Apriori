# JavaUtil - RELEASE NOTES

## Version 1.0.0 (Jun. 17th 2017)

The first stable release of the library, which provides the following features:

- Allows to find frequent item sets and (optionally) to generate association rules using the Apriori algorithm
- The Apriori algorithm can be configured using the builder pattern
- The algorithm can be configured to try to find a specific number of frequent item sets and/or association rules
- Rule sets can be ordered and filtered by support, confidence, lift and leverage
- Logging is provided by using the SLF4J logging facade