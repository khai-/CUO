package Support;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CustomCellRenderer implements ListCellRenderer{
	public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus){
		Component component=(Component)value;
		if(isSelected==false){
			component.setBackground(Color.WHITE);
		}else if(isSelected==true){
			component.setBackground(Color.LIGHT_GRAY);
		}
		return component;
	}
}
