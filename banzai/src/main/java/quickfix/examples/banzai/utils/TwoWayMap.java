/*******************************************************************************
 * Copyright (c) quickfixengine.org All rights reserved.
 *
 * This file is part of the QuickFIX FIX Engine
 *
 * This file may be distributed under the terms of the quickfixengine.org license as defined by
 * quickfixengine.org and appearing in the file LICENSE included in the packaging of this file.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE WARRANTY OF DESIGN,
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See http://www.quickfixengine.org/LICENSE for licensing information.
 *
 * Contact ask@quickfixengine.org if any conditions of this licensing are not clear to you.
 ******************************************************************************/

package quickfix.examples.banzai.utils;

import java.util.HashMap;

public class TwoWayMap<T1, T2> {
  private final HashMap<T1, T2> firstToSecond = new HashMap<>();
  private final HashMap<T2, T1> secondToFirst = new HashMap<>();

  public void put(final T1 first, final T2 second) {
    this.firstToSecond.put(first, second);
    this.secondToFirst.put(second, first);
  }

  public T2 getFirst(final T1 first) {
    return this.firstToSecond.get(first);
  }

  public T1 getSecond(final T2 second) {
    return this.secondToFirst.get(second);
  }

  public String toString() {
    return this.firstToSecond.toString() + "\n" + this.secondToFirst.toString();
  }
}
