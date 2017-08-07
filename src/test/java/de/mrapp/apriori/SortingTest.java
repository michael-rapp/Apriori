package de.mrapp.apriori;

import de.mrapp.apriori.metrics.Support;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the functionality of the class {@link Sorting}.
 *
 * @author Michael Rapp
 */
public class SortingTest {

    /**
     * Tests the default sorting for item sets.
     */
    @Test
    public void testDefaultSortingForItemSets() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.setSupport(0.5);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.setSupport(0.5);
        Sorting<ItemSet> sorting = Sorting.forItemSets();
        assertEquals(0, sorting.compare(itemSet1, itemSet2));
        itemSet1.setSupport(0.6);
        assertEquals(-1, sorting.compare(itemSet1, itemSet2));
        itemSet1.setSupport(0.4);
        assertEquals(1, sorting.compare(itemSet1, itemSet2));
    }

    /**
     * Tests the sorting for item sets, when sorting in ascending order.
     */
    @Test
    public void testAscendingSortingForItemSets() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.setSupport(0.5);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.setSupport(0.5);
        Sorting<ItemSet> sorting = Sorting.forItemSets().withOrder(Sorting.Order.ASCENDING);
        assertEquals(0, sorting.compare(itemSet1, itemSet2));
        itemSet1.setSupport(0.6);
        assertEquals(1, sorting.compare(itemSet1, itemSet2));
        itemSet1.setSupport(0.4);
        assertEquals(-1, sorting.compare(itemSet1, itemSet2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the sorting for item sets, if
     * the order is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSortingForItemSetsThrowsExceptionIfOrderIsNull() {
        Sorting.forItemSets().withOrder(null);
    }

    /**
     * Tests the sorting for item sets, when performing tie-breaking.
     */
    @Test
    public void testSortingForItemSetsWhenTieBreaking() {
        ItemSet<NamedItem> itemSet1 = new ItemSet<>();
        itemSet1.setSupport(0.5);
        ItemSet<NamedItem> itemSet2 = new ItemSet<>();
        itemSet2.setSupport(0.5);
        Sorting<ItemSet> sorting = Sorting.forItemSets().withTieBreaking((o1, o2) -> 1);
        assertEquals(-1, sorting.compare(itemSet1, itemSet2));
    }

    /**
     * Tests the default sorting for association rules.
     */
    @Test
    public void testDefaultSortingForAssociationRules() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.25);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        body2.setSupport(0.5);
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.25);
        Sorting<AssociationRule> sorting = Sorting.forAssociationRules();
        assertEquals(0, sorting.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(-1, sorting.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.1);
        assertEquals(1, sorting.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the sorting for association rules, when sorting in ascending order.
     */
    @Test
    public void testAscendingSortingForAssociationRules() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.25);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        body2.setSupport(0.5);
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.25);
        Sorting<AssociationRule> sorting = Sorting.forAssociationRules().withOrder(
                Sorting.Order.ASCENDING);
        assertEquals(0, sorting.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(1, sorting.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.1);
        assertEquals(-1, sorting.compare(associationRule1, associationRule2));
    }

    /**
     * Ensures, that an {@link IllegalArgumentException} is thrown by the sorting for association
     * rules, if the order is null.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSortingForAssociationRulesThrowsExceptionIfOrderIsNull() {
        Sorting.forAssociationRules().withOrder(null);
    }

    /**
     * Tests the sorting for association rules, when performing tie-breaking.
     */
    @Test
    public void testSortingForAssociationRulesWhenTieBreaking() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.25);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        body2.setSupport(0.5);
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.25);
        Sorting<AssociationRule> sorting = Sorting.forAssociationRules()
                .withTieBreaking((o1, o2) -> 1);
        assertEquals(-1, sorting.compare(associationRule1, associationRule2));
    }

    /**
     * Tests the sorting for association rules, when sorting according to a specific operator.
     */
    @Test
    public void testSortingByOperatorForAssociationRules() {
        ItemSet<NamedItem> body1 = new ItemSet<>();
        body1.add(new NamedItem("a"));
        body1.setSupport(0.5);
        ItemSet<NamedItem> head1 = new ItemSet<>();
        head1.add(new NamedItem("b"));
        AssociationRule<NamedItem> associationRule1 = new AssociationRule<>(body1, head1, 0.25);
        ItemSet<NamedItem> body2 = new ItemSet<>();
        body2.add(new NamedItem("c"));
        body2.setSupport(0.5);
        ItemSet<NamedItem> head2 = new ItemSet<>();
        head2.add(new NamedItem("d"));
        AssociationRule<NamedItem> associationRule2 = new AssociationRule<>(body2, head2, 0.25);
        Sorting<AssociationRule> sorting = Sorting.forAssociationRules().byOperator(new Support());
        assertEquals(0, sorting.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.5);
        assertEquals(-1, sorting.compare(associationRule1, associationRule2));
        associationRule1 = new AssociationRule<>(body1, head1, 0.1);
        assertEquals(1, sorting.compare(associationRule1, associationRule2));
    }

}