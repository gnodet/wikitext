/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Jan 31, 2005
 */

/**
 * @author Mik Kersten
 */
public class Foo {
    
    int y = 0;
    
    public void setY(int y) {
        this.y = y;
        Display.update();
    }
    
    public void setX(int x) { }
    
    public void setP1(<field> p) { }
    
    public void setP2(<field> p) { } 

}

class <field> { } 

class Display { static void update() {}  }

class Point { } 

class Line { } 