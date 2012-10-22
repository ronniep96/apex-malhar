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
 * Takes in one stream via input port "data". Each tuple is tested for the compare function. The function is given by
 * "key", "value", and "compare". If all tuples passes a Boolean(true) is emitted, else a Boolean(false) is emitted on end of window on the output port "all".
 * The comparison is done by getting double value from the Number.<p>
 * This module is an end of window module<br>
 * <br>
 * Ports:<br>
 * <b>data</b>: Input port, expects HashMap<K,V extends Number><br>
 * <b>all</b>: Output port, emits Boolean<br>
 * <br>
 * Properties:<br>
 * <b>key</b>: The key on which compare is done<br>
 * <b>value</b>: The value to compare with<br>
 * <b>comp<b>: The compare function. Supported values are "lte", "lt", "eq", "neq", "gt", "gte". Default is "eq"<br>
 * <br>
 * Compile time checks<br>
 * Key must be non empty/null<br>
 * Value must be able to convert to a "double"<br>
 * Compare string, if specified, must be one of "lte", "lt", "eq", "neq", "gt", "gte"<br>
 * <br>
 * Run time checks<br>
 * none<br>
 * <br>
 * <b>Benchmarks</b>: Blast as many tuples as possible in inline mode<br>
 *
 * @author amol
 */
public class MatchAll<K, V extends Number> extends BaseMatchOperator<K>
{
  public final transient DefaultInputPort<HashMap<K, V>> data = new DefaultInputPort<HashMap<K, V>>(this)
  {
    @Override
    public void process(HashMap<K, V> tuple)
    {
      if (!result) {
        return;
      }
      V val = tuple.get(key);
      if (val == null) { // skip if key does not exist
        return;
      }
      double tvalue = val.doubleValue();
      result = ((type == supported_type.LT) && (tvalue < value))
                || ((type == supported_type.LTE) && (tvalue <= value))
                || ((type == supported_type.EQ) && (tvalue == value))
                || ((type == supported_type.NEQ) && (tvalue != value))
                || ((type == supported_type.GT) && (tvalue > value))
                || ((type == supported_type.GTE) && (tvalue >= value));
    }
  };
  public final transient DefaultOutputPort<Boolean> all = new DefaultOutputPort<Boolean>(this);
  Boolean result = true;

  @Override
  public void beginWindow()
  {
    result = true;
  }

  @Override
  public void endWindow()
  {
    all.emit(result);
  }
}
