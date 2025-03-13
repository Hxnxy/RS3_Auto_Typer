package org.sexydock.tabs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;

public class DefaultTabsRemovedHandler extends ContainerAdapter
{
	@Override
	public void componentRemoved( ContainerEvent e )
	{
		if( e.getContainer( ) instanceof JTabbedPane)
		{
			JTabbedPane tabbedPane = (JTabbedPane) e.getContainer( );
			if( tabbedPane.getTabCount( ) == 0 )
			{
				Window ancestor = SwingUtilities.getWindowAncestor( tabbedPane );
				if( ancestor != null )
				{
					ancestor.dispose( );
				}
			}
		}
	}
}
