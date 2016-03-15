/*
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.windows.speech;

import org.luwrain.speech.BackEnd;

public class SAPI implements BackEnd
{
    private static final String SAPI_ENGINE_PREFIX = "--sapi-engine=";

    private int defaultPitch = 50;
    private int defaultRate = 50;

    SAPIImpl impl=new SAPIImpl();
    
    private String getAttrs(String[] cmdLine)
    {
	if (cmdLine == null)
	    return null;
	for(String s: cmdLine)
	{
	    if (s == null)
		continue;
	    if (s.startsWith(SAPI_ENGINE_PREFIX))
		return s.substring(SAPI_ENGINE_PREFIX.length());
	}
	return null;
    }

    @Override public String init(String[] cmdLine)
    {
	final String attrs = getAttrs(cmdLine);
	if (attrs == null || attrs.trim().isEmpty())
	    System.out.println("Initializing SAPI with default parameters"); else
	    System.out.println("Initializing SAPI with the following arguments: " + attrs);
	impl.searchVoiceByAttributes(attrs);
	System.out.println("getNextVoiceIdFromList()=" + impl.getNextVoiceIdFromList());
	System.out.println("selectCurrentVoice()=" + impl.selectCurrentVoice());
	return null;
    }

    public void close()
    {
    }

    @Override public void say(String text)
    {
	say(text, defaultPitch, defaultRate);
    }

    @Override public void say(String text, int pitch)
    {
	say(text, pitch, defaultRate);
    }

    @Override public void say(String text,
			      int pitch,
			      int rate)
    {
    	impl.speak(encodeXml(text, pitch, rate));
    }

    @Override public void sayLetter(char letter)
    {
	sayLetter(letter, defaultPitch, defaultRate);
    }

    @Override public void sayLetter(char letter, int pitch)
    {
	sayLetter(letter, pitch, defaultRate);
    }

    @Override public void sayLetter(char letter,
				    int pitch,
				    int rate)
    {
	String s = "";
	if (Character.isUpperCase(letter))
s = encodeXml("" + letter, 100, rate); else
s = encodeXml("" + letter, pitch, rate);
	//	    SAPIImpl.speak("<spell>" + s + "</spell>");
	impl.speak(s);
    }

    public void silence()
    {
    	impl.speak("", SAPIImpl.SPF_PURGEBEFORESPEAK);
    }

    @Override public void setDefaultPitch(int value)
    {
	defaultPitch = value;
    }

    @Override public void setDefaultRate(int value)
    {
	defaultRate = value;
    }

    private static String encodeXml(String text, int pitch, int rate)
    {
	String s = "";
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    if (Character.isLetter(c) || Character.isSpace(c))
	    {
		s += c;
		continue;
	    }
	    switch(c)
	    {
	    case '&':
		s += "<spell>&amp;</spell>";
		break;
	    case '<':
		s += "<spell>&lt;</spell>";
		break;
	    case '>':
		s += "<spell>&gt;</spell>";
		break;
	    case '\"':
		s += "<spell>&quot;</spell>";
		break;
	    default:
		s += "<spell>" + c + "</spell>";
	    }
	}
	final int r = (rate / 5) - 10;
	final int p = (pitch / 5) - 10;
	s = "<rate absspeed=\"" + r + "\"/>" + s;
	s = "<pitch absmiddle=\"" + p + "\"/>" + s;
	return s;
    }

    private static String encodeXmlNoSpell(String text, int pitch, int rate)
    {
	String s = "";
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    if (Character.isLetter(c) || Character.isSpace(c))
	    {
		s += c;
		continue;
	    }
	    switch(c)
	    {
	    case '&':
		s += "&amp;";
		break;
	    case '<':
		s += "&lt;";
		break;
	    case '>':
		s += "&gt;";
		break;
	    case '\"':
		s += "&quot;";
		break;
	    default:
		s += c;
	    }
	}
	final int r = (rate / 5) - 10;
	final int p = (pitch / 5) - 10;
	s = "<rate absspeed=\"" + r + "\"/>" + s;
	s = "<pitch absmiddle=\"" + p + "\"/>" + s;
	return s;
    }
}