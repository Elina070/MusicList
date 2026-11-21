
package edu.java.app;

import org.junit.Test;
import org.junit.Assert;

public class MusicGroupListTest {

    @Test
    public void testAddGroup() {
        MusicGroupList list = new MusicGroupList();

        int before = list.getGroupCount();
        list.addGroup();

        Assert.assertEquals(before + 1, list.getGroupCount());
    }

    @Test
    public void testDeleteGroup() {
        MusicGroupList list = new MusicGroupList();

        int before = list.getGroupCount();
        list.deleteGroup(1);

        Assert.assertEquals(before - 1, list.getGroupCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteGroupInvalidIndex() {
        MusicGroupList list = new MusicGroupList();
        list.deleteGroup(-1);
    }

    @Test
    public void testFindGroup() {
        MusicGroupList list = new MusicGroupList();

        Assert.assertEquals(1, list.findGroup("queen"));
        Assert.assertEquals(2, list.findGroup("Nir"));
        Assert.assertEquals(-1, list.findGroup("Metallica"));
    }

    @Test
    public void testFindGroupEmptyQuery() {
        MusicGroupList list = new MusicGroupList();

        Assert.assertEquals(-1, list.findGroup(""));
        Assert.assertEquals(-1, list.findGroup(null));
    }

    @Test
    public void testGetGroupName() {
        MusicGroupList list = new MusicGroupList();

        Assert.assertEquals("The Beatles", list.getGroupName(0));
    }

    @Test
    public void testIsCellEditable() {
        MusicGroupList list = new MusicGroupList();

        Assert.assertTrue(list.isCellEditable(0, 6));
        Assert.assertFalse(list.isCellEditable(0, 7));
    }
}
