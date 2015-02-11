/*
 * WUMprep4Weka - WUMprep for the WEKA data mining environment
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/* 
 * WUMprepConfigurationEditor.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep4weka.gui;

import java.awt.Component;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;

import javax.swing.JPanel;

import org.hypknowsys.wumprep.config.WUMprepConfiguration;

import weka.gui.CustomPanelSupplier;

/**
 * Provides a {@link java.beans.PropertyEditor} for a JavaBean's
 * {@link WUMprepConfiguration} property.
 * <p>
 * This editor has been developed and tested solely for use by the Weka
 * Knowledge Flow GUI. It might need some modifications or even a rewrite for
 * use by general JavaBean-aware IDEs.
 * 
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: WUMprepConfigurationEditor.java,v 1.3 2005/10/16 13:24:32 cpohle Exp $
 */
public class WUMprepConfigurationEditor extends PropertyEditorSupport implements
		PropertyChangeListener, CustomPanelSupplier {

	/**
	 * The configuration edited by <code>this</code>
	 */
	WUMprepConfiguration config = null;

	/**
	 * The <code>JPanel</code> holding <code>this</code>.
	 */
	JPanel objectPropertyPanel = null;

	public Component getCustomEditor() {
		return (Component) getObjectPropertyPanel();
	}

	public boolean supportsCustomEditor() {
		return true;
	}

	/**
	 * Gets the <code>objectPropertyPanel</code>.
	 * 
	 * @return The object property panel.
	 */
	private JPanel getObjectPropertyPanel() {
		if (objectPropertyPanel == null) {
			objectPropertyPanel = new WUMprepConfigurationCustomizer();
			objectPropertyPanel.addPropertyChangeListener(this);
		}

		return objectPropertyPanel;
	}

	public void setValue(Object arg0) {
		// super.setValue(arg0);
		config = (WUMprepConfiguration) arg0;
		((Customizer) getObjectPropertyPanel()).setObject(arg0);
	}

	public Object getValue() {
		return config;

	}

	public boolean isPaintable() {
		return true;
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getNewValue() instanceof WUMprepConfiguration)
			config = (WUMprepConfiguration) arg0.getNewValue();
		firePropertyChange();
	}

	public JPanel getCustomPanel() {
		return (JPanel) getCustomEditor();
	}
}
