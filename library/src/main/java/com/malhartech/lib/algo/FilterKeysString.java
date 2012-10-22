/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.lib.algo;

import com.malhartech.api.BaseOperator;
import com.malhartech.api.DefaultInputPort;
import com.malhartech.api.DefaultOutputPort;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Takes a stream on input port "data", and outputs only keys specified by property "keys" on put output port "filter". If
 * property "inverse" is set to "true", then all keys except those specified by "keys" are emitted<p>
 * <br>
 * This is a pass through node. It takes in HashMap<String, V> and outputs HashMap<String, V><br>
 * <br>
 * <b>Ports</b>
 * <b>data</b>: Input data port expects HashMap<String, V>
 * <b>filter</b>: Output data port, emits HashMap<String, V>
 * <b>Properties</b>:
 * <b>keys</b>: The keys to pass through, rest are filtered/dropped. A comma separated list of keys<br>
 * <b>Benchmarks></b>: TBD<br>
 * Compile time checks are:<br>
 * <b>keys</b> cannot be empty<br>
 * <br>
 * Run time checks are:<br>
 * None
 * <br>
 *
 * @author amol<br>
 *
 */

public class FilterKeysString<V> extends BaseOperator
{
  public final transient DefaultInputPort<HashMap<String, V>> data = new DefaultInputPort<HashMap<String, V>>(this)
  {
    @Override
    public void process(HashMap<String, V> tuple)
    {
      HashMap<String, V> dtuple = null;
      for (Map.Entry<String, V> e: tuple.entrySet()) {
        boolean contains = keys.containsKey(e.getKey());
        if ((contains && !inverse) || (!contains && inverse)) {
          if (dtuple == null) {
            dtuple = new HashMap<String, V>(4); // usually the filter keys are very few, so 4 is just fine
          }
          dtuple.put(e.getKey(), e.getValue());
        }
      }
      if (dtuple != null) {
        filter.emit(dtuple);
      }
    }
  };
  public final transient DefaultOutputPort<HashMap<String, V>> filter = new DefaultOutputPort<HashMap<String, V>>(this);

  HashMap<Object, V> keys = new HashMap<Object, V>();
  boolean inverse = false;

  public void setInverse(boolean val) {
    inverse = val;
  }

  public void setKeys(String str) {
    String[] klist = str.split(",");
    for (String k : klist) {
      keys.put(k, null);
    }
  }
}
