/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.values;

import java.util.Collection;

/**
 * Value that contains more primitive component values
 */
public interface IAggregateValue
    extends IValue
{
    /**
     * @return direct components of this aggregate values. If value component is also IAggregateParameter
     * than its child components are not returned.
     */
    Collection<IValue> getComponents();
}
