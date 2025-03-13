package org.sexydock;

import org.sexydock.tabs.jhrome.JhromeTabbedPaneUI;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class SwingUtils
{
	public static void doSwing( Runnable r )
	{
		if( SwingUtilities.isEventDispatchThread( ) )
		{
			r.run( );
		}
		else
		{
			try
			{
				SwingUtilities.invokeAndWait( r );
			}
			catch( InvocationTargetException e )
			{
				throw new RuntimeException( e );
			}
			catch( InterruptedException e )
			{
				throw new RuntimeException( e );
			}
		}
	}
	
	public static JTabbedPane getJTabbedPaneAncestor(Component c )
	{
		while( c != null )
		{
			if( c instanceof JTabbedPane)
			{
				return (JTabbedPane) c;
			}
			c = c.getParent( );
		}
		return null;
	}
	
	public static JhromeTabbedPaneUI getJTabbedPaneAncestorUI( Component c )
	{
		JTabbedPane tabbedPane = getJTabbedPaneAncestor( c );
		if( tabbedPane != null && tabbedPane.getUI( ) instanceof JhromeTabbedPaneUI )
		{
			return ( JhromeTabbedPaneUI ) tabbedPane.getUI( );
		}
		return null;
	}
}
