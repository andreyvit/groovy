package groovy.swt.examples

import groovy.swt.SwtBuilder

class TrayDemo {
    
    @Property swt
    @Property shell
        
    def run() {
        swt = new SwtBuilder()
        
        shell = swt.shell ( text:'The tray Demo' ) {
         	
         	def trayMenu = menu {
         		menuItem( text:"menuItem1" )
         		menuItem( text:"menuItem2" )
         	}
         	
         	tray() {
	         	
         		trayItem( text:"trayItem1" ) {
	         		image( src:"src/test/groovy/swt/groovy-logo.png" ) 
	         		
	         		onEvent( type:"Selection", closure:{
	         			println "Selection event ..."
	         		})
	         		onEvent( type:"Hide", closure:{
	         			println "Hide event ..."
	         		})
	         		onEvent( type:"DefaultSelection", closure:{
	         			println "DefaultSelection event ..."
	         		})
	         		onEvent( type:"Show", closure:{
	         			println "Show event ..."
	         		})
	         		onEvent( type:"MenuDetect", closure:{
	         			println "MenuDetect event ..."
	         			trayMenu.visible = true
	         		})
         		}
         	}
        }
        
		shell.open()
	
		while(! shell.isDisposed()) { 
			if (! shell.display.readAndDispatch()) {
				shell.display.sleep();
			}
		}
			
	}
}
