/*
Copyright (C) 2015 Martin Frické (mfricke@email.arizona.edu https://softoption.us mfricke@softoption.us mfricke1947@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation 
files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, 
modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the 
Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package us.softoption.editor;


// SimpleLinkListener.java
// A hyperlink listener for use with JEditorPane.  This
// listener changes the cursor over hyperlinks based on enter/exit
// events and also loads a new page when a valid hyperlink is clicked.
//

import java.io.FileNotFoundException;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

public class TSimpleLinkListener implements HyperlinkListener {

  private JEditorPane pane;       // The pane we're using to display HTML

  private JTextField  urlField;   // An optional text field for showing
                                  // the current URL being displayed

  private JLabel statusBar;       // An optional label for showing where
                                  // a link would take you

  public TSimpleLinkListener(JEditorPane jep, JTextField jtf, JLabel jl) {
    pane = jep;
    urlField = jtf;
    statusBar = jl;
  }

  public TSimpleLinkListener(JEditorPane jep) {
    this(jep, null, null);
  }

  public void hyperlinkUpdate(HyperlinkEvent he) {
    HyperlinkEvent.EventType type = he.getEventType();
    if (type == HyperlinkEvent.EventType.ENTERED) {
      // Enter event.  Fill in the status bar.
      if (statusBar != null) {
        statusBar.setText(he.getURL().toString());
      }
    }
    else if (type == HyperlinkEvent.EventType.EXITED) {
      // Exit event.  Clear the status bar.
      if (statusBar != null) {
        statusBar.setText(" "); // Must be a space or it disappears
      }
    }
    else if (type == HyperlinkEvent.EventType.ACTIVATED) {
      // Jump event.  Get the URL, and, if it's not null, switch to that
      // page in the main editor pane and update the "site url" label.
      if (he instanceof HTMLFrameHyperlinkEvent) {
        // Ahh, frame event; handle this separately.
        HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)he;
        HTMLDocument doc = (HTMLDocument)pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent(evt);
      } else {
        try {
          pane.setPage(he.getURL());
          if (urlField != null) {
            urlField.setText(he.getURL().toString());
          }
        }
        catch (FileNotFoundException fnfe) {
          pane.setText("Could not open file: <tt>" + he.getURL() +
                       "</tt>.<hr>");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
