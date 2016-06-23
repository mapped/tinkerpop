/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.tinkerpop.gremlin.python.jsr223;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.Test;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GremlinJythonScriptEngineTest {

    @Test
    public void shouldGetEngineByName() throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("gremlin-jython");
        assertNotNull(engine);
        assertTrue(engine instanceof GremlinJythonScriptEngine);
        assertEquals(3, engine.eval("1+2"));
    }

    @Test
    public void shouldHaveCoreImports() throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("gremlin-jython");
        assertTrue(engine.eval("Graph") instanceof Class);
        assertTrue(engine.eval("__") instanceof Class);
        assertTrue(engine.eval("T") instanceof Class);
        assertTrue(engine.eval("label") instanceof T);
        assertTrue(engine.eval("T.label") instanceof T);
        assertTrue(engine.eval("out()") instanceof GraphTraversal);
        assertTrue(engine.eval("__.out()") instanceof GraphTraversal);
    }


    @Test
    public void shouldSupportJavaBasedGraphTraversal() throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("gremlin-jython");
        final Bindings bindings = engine.createBindings();
        bindings.put("graph", TinkerFactory.createModern());
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        engine.eval("g = graph.traversal()");
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(out()).times(2).values('name').toSet()"));
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(__.out()).times(2).values('name').toSet()"));
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(out()).times(2).name.toSet()"));
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(__.out()).times(2).name.toSet()"));
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(__.out()).times(2)[0:2].name.toSet()"));
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(__.out()).times(2).name[0:3].toSet()"));
    }

    @Test
    public void shouldSupportSugarMethods() throws Exception {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("gremlin-jython");
        final Bindings bindings = engine.createBindings();
        bindings.put("graph", TinkerFactory.createModern());
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        engine.eval("g = graph.traversal()");
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(__.out()).times(2)[0:2].name.toSet()"));
        assertEquals(new HashSet<>(Arrays.asList("ripple", "lop")), engine.eval("g.V().repeat(__.out()).times(2).name[0:3].toSet()"));
        assertEquals(BigInteger.valueOf(1), engine.eval("g.V().repeat(__.out()).times(2).name[0:1].count().next()"));
        assertEquals(BigInteger.valueOf(1), engine.eval("g.V().repeat(__.out()).times(2).name[0].count().next()"));
        assertEquals(BigInteger.valueOf(0), engine.eval("g.V().repeat(__.out()).times(2).name[3].count().next()"));
    }
}
