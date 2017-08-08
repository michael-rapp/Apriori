# Apriori - RELEASE NOTES

## Version 1.2.0 (Aug. 8th 2017)

A feature release, which introduces the following changes:

- An enhanced API for sorting and filtering association rules and frequent item sets has been added
- The class `AssociationRule` does now provide `covers`-methods, which allow to test, if the rule applies to a set of items

## Version 1.1.1 (Jul. 31th 2017)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/Apriori/issues/3

## Version 1.1.0 (Jul. 30th 2017)

A feature release, which introduces the following changes:

- Added the ability to use different tie-breaking strategies when sorting or filtering rule sets

## Version 1.0.1 (Jul. 28th 2017)

A bugfix release, which fixes the following issues:

- https://github.com/michael-rapp/Apriori/issues/1

## Version 1.0.0 (Jun. 16th 2017)

The first stable release of the library, which provides the following features:

- Allows to find frequent item sets and (optionally) to generate association rules using the Apriori algorithm
- The Apriori algorithm can be configured using the builder pattern
- The algorithm can be configured to try to find a specific number of frequent item sets and/or association rules
- Rule sets can be ordered and filtered by support, confidence, lift and leverage
- Logging is provided by using the SLF4J logging facade