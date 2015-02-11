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
 * About.java
 * Copyright (C) 2005 Carsten Pohle (cp AT cpohle de)
 */


package org.hypknowsys.wumprep.configeditor.help;

import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Carsten Pohle (cp AT cpohle de)
 * @version $Id: About.java,v 1.3 2005/10/16 13:25:42 cpohle Exp $
 */
public class About extends JFrame {

	private JPanel jContentPane = null;
	private ImageIcon wumPrepImageIcon = null;  //  @jve:decl-index=0:visual-constraint="446,46"
	private JLabel splashScreenjLabel = null;

	/**
	 * This is the default constructor
	 */
	public About() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(450, 300);
		this.setContentPane(getJContentPane());
		this.setTitle("About WUMprep Configuration Editor");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			splashScreenjLabel = new JLabel();
			splashScreenjLabel.setText("JLabel");
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			jContentPane = new JPanel();
			jContentPane.setLayout(gridLayout);
			jContentPane.add(splashScreenjLabel, getWumPrepImageIcon());;
			
			
		}
		return jContentPane;
	}

	/**
	 * This method initializes wumPrepImageIcon	
	 * 	
	 * @return javax.swing.ImageIcon	
	 */
	private ImageIcon getWumPrepImageIcon() {
		if (wumPrepImageIcon == null) {
			wumPrepImageIcon = new ImageIcon();
		}
		return wumPrepImageIcon;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
