/*
 *  Copyright (c) 2012 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.malhartech.lib.algo;

import com.malhartech.api.DefaultInputPort;
import com.malhartech.api.DefaultOutputPort;
import java.util.HashMap;

/**
 *
 * Takes in one stream via input port "data". A compare function is imposed based on the property "key", "value", and "compare". All tuples
 * are emitted (inclusive) once a match is made. The comparison is done by getting double value from the Number.<p>
 * This module is a pass through<br>
 * <br>
 * Ports:<br>
 * <b>data</b>: Input port, expects HashMap<K, String><br>
 * <b>allafter</b>: Output port, emits HashMap<K, String> if compare function returns true<br>
 * <br>
 * Properties:<br>
 * <b>key</b>: The key on which compare is done<br>
 * <b>value</b>: The value to compare with<br>
 * <b>comp<b>: The compare function. Supported values are "lte", "lt", "eq", "neq", "gt", "gte". Default is "eq"<br>
 * <br>
 * Compile time checks<br>
 * Key must be non empty<br>
 * Value must be able to convert to a "double"<br>
 * Compare string, if specified, must be one of "lte", "lt", "eq", "neq", "gt", "gte"<br>
 * <br>
 * Run time checks<br>
 * none<br>
 * <br>
 * <b>Benchmarks</b>: Blast as many tuples as possible in inline mode<br>
 * Integer: ?? million tuples/s<br>
 * Double: ?? million tuples/s<br>
 * Long: ?? million tuples/s<br>
 * Short: ?? million tuples/s<br>
 * Float: ?? million tuples/s<br>
 *
 * @author amol
 */
public class AllAfterMatchStringValue<K, String> extends BaseMatchOperator<K>
{
  public final transient DefaultInputPort<HashMap<K, String>> data = new DefaultInputPort<HashMap<K, String>>(this)
  {
    @Override
    public void process(HashMap<K, String> tuple)
    {
      if (doemit) {
        allafter.emit(tuple);
        return;
      }
      String val = tuple.get(key);
      if (val == null) { // error tuple
        return;
      }
      double tvalue = 0;
      boolean error = false;
      try {
        tvalue = Double.valueOf(val.toString()).doubleValue();
      }
      catch (NumberFormatException e) {
        error = true;
      }
      if (!error) {
        if (((type == supported_type.LT) && (tvalue < value))
                || ((type == supported_type.LTE) && (tvalue <= value))
                || ((type == supported_type.EQ) && (tvalue == value))
                || ((type == supported_type.NEQ) && (tvalue != value))
                || ((type == supported_type.GT) && (tvalue > value))
                || ((type == supported_type.GTE) && (tvalue >= value))) {
          doemit = true;
          allafter.emit(tuple);
        }
      }
    }
  };
  public final transient DefaultOutputPort<HashMap<K, String>> allafter = new DefaultOutputPort<HashMap<K, String>>(this);
  boolean doemit = false;

  @Override
  public void beginWindow()
  {
    doemit = false;
  }
}
