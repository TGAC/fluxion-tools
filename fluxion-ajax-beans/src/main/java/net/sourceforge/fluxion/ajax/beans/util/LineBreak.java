package net.sourceforge.fluxion.ajax.beans.util;

/**
 * Created by IntelliJ IDEA. User: davey Date: 13-Nov-2009 Time: 12:11:17 To change this template use File | Settings |
 * File Templates.
 */
public enum LineBreak {
      HTML ("<br/>"),
      STANDARD ("\n");

      private final String linebreak;
      LineBreak(String linebreak) {
          this.linebreak = linebreak;
      }
      public String getBreak() { return linebreak; }
}
