package com.thebluealliance.androidclient.database;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.util.List;

import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public final class DbTableTestDriver {

    private DbTableTestDriver() {
        // unused
    }

    public static <T extends TbaDatabaseModel> void testNullValues(ModelTable<T> table) {
        T model = null;
        ImmutableList<T> models = null;
        long ret1 = table.add(model);
        long ret2 = table.update(model);
        int ret3 = table.add(models);

        assertEquals(-1, ret1);
        assertEquals(-1, ret2);
        assertEquals(0, ret3);
    }

    public static <T extends TbaDatabaseModel> void testAddAndGet(ModelTable<T> table, T model,
                                                                  Gson gson) {
        assertFalse(table.exists(model.getKey()));
        long ret = table.add(model);
        verify(table).insertCallback(model);

        T result = table.get(model.getKey());
        assertNotEquals(-1, ret);
        assertNotNull(result);
        assertEquals(model.getParams(gson), result.getParams(gson));
        assertTrue(table.exists(model.getKey()));

        // Ensure we can fetch a projection correctly
        T result2 = table.get(model.getKey(), new String[]{table.getKeyColumn()});
        assertNotNull(result2);
        assertNotNull(result2.getKey());
        assertEquals(model.getKey(), result2.getKey());

        // Test fetching a record that doesn't exist
        T badResult = table.get("meow");
        assertNull(badResult);
        assertFalse(table.exists("meow"));

        badResult = table.get("meow", new String[]{table.getKeyColumn()});
        assertNull(badResult);
    }

    public static <T extends TbaDatabaseModel> void testAddAndGetList(ModelTable<T> table,
                                                                      ImmutableList<T> models) {
        // Test what happens with no records
        List<T> result = table.getAll();
        assertNotNull(result);
        assertTrue(result.isEmpty());

        int ret = table.add(models);
        assertEquals(models.size(), ret);
        for (T model : models) {
            verify(table).insertCallback(model);
        }

        result = table.getAll();
        assertNotNull(result);
        assertEquals(models.size(), result.size());
    }

    public static <T extends TbaDatabaseModel> T testUpdate(ModelTable<T> table,
                                                            T model,
                                                            Action1<T> callback,
                                                            Gson gson) {
        table.add(model);

        T result = table.get(model.getKey());
        assertNotNull(result);
        assertEquals(model.getParams(gson), result.getParams(gson));

        callback.call(result);
        int affected = table.update(result);
        assertEquals(1, affected);
        verify(table).updateCallback(result);

        T result2 = table.get(result.getKey());
        assertNotNull(result2);
        assertNotEquals(model.getParams(gson), result2.getParams(gson));
        assertEquals(result.getParams(gson), result2.getParams(gson));
        return result2;
    }

    public static <T extends TbaDatabaseModel> void testDelete(ModelTable<T> table,
                                                               List<T> models,
                                                               String where,
                                                               String[] whereArgs,
                                                               int affectedRows) {
        // Test with one model
        T model = models.get(0);
        table.add(model);
        assertTrue(table.exists(model.getKey()));

        table.delete(model);
        assertFalse(table.exists(model.getKey()));
        assertNull(table.get(model.getKey()));
        verify(table).deleteCallback(model);

        // Test delete by a where clause
        int count = table.add(ImmutableList.copyOf(models));
        assertEquals(models.size(), count);

        count = table.delete(where, whereArgs);
        assertEquals(count, affectedRows);

        // Test delete all
        table.deleteAllRows();
        verify(table).deleteAllCallback();
        List<T> remaining = table.getAll();
        assertTrue(remaining.isEmpty());
    }
}
