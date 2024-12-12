package de.hma.srn.domain;

import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;
import org.jline.reader.impl.DefaultParser;

public class CustomParser extends DefaultParser {

    @Override
    public ParsedLine parse(String arg0, int arg1, ParseContext arg2) throws SyntaxError {
        String verarbeiteteLine = arg0.replace("\\", "\\\\");

        ParsedLine parsedLine = super.parse(verarbeiteteLine, arg1, arg2);
        return parsedLine;
    };

}
