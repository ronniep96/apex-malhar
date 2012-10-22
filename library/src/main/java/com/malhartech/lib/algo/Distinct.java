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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Takes a stream via input port "data" and emits distinct key,val pairs (i.e drops duplicates) on output port "distinct". Restarts at end of window boundary<p>
 * <br>
 * This module is same as a "FirstOf" operation on any key, val pair
 * Even though this module produces continuous tuples, at end of window all data is flushed. Thus the data set is windowed
 * and no history is kept of previous windows<br>
 * <br>
 * <b>Ports</b>
 * <b>data</b>: Input data port expects HashMap<K,V>
 * <b>distinct</b>: Output data port, emits HashMap<K,V>(1)
 * <b>Properties</b>:
 * None
 * <b>Benchmarks></b>: TBD<br>
 * Compile time checks are:<br>
 * None
 * <br>
 * Run time checks are:<br>
 * None as yet
 *
 *
 * @author amol<br>
 *
 */

public class Distinct<K,V> extends BaseOperator
{
  public final transient DefaultInputPort<HashMap<K,V>> data = new DefaultInputPort<HashMap<K,V>>(this)
  {
    @Override
    public void process(HashMap<K,V> tuple)
    {
      for (Map.Entry<K,V> e: tuple.entrySet()) {
        HashMap<V, Object> vals = mapkeyval.get(e.getKey());
        if ((vals == null) || !vals.containsKey(e.getValue())) {
          HashMap<K,V> otuple = new HashMap<K,V>(1);
          otuple.put(e.getKey(), e.getValue());
          distinct.emit(otuple);
          if (vals == null) {
            vals = new HashMap<V, Object>();
            mapkeyval.put(e.getKey(), vals);
          }
          vals.put(e.getValue(), null);
        }
      }
    }
  };
  public final transient DefaultOutputPort<HashMap<K,V>> distinct = new DefaultOutputPort<HashMap<K,V>>(this);
  HashMap<K, HashMap<V, Object>> mapkeyval = new HashMap<K, HashMap<V, Object>>();

  @Override
  public void beginWindow()
  {
    mapkeyval.clear();
  }
}
