package org.apache.maven.plugins.shade;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Jakub Solecki
 */
public class InstrumentationMatcher
{
    private final Set<String> jarNames = new HashSet<>();

    public void addJarName( String jarName )
    {
        jarNames.add( jarName );
    }

    public Set<String> getJarNames()
    {
        return jarNames;
    }

    public void searchForInstrumentationRules()
    {
        try
        {
            for ( String name : jarNames )
            {
                int index = name.lastIndexOf( "-" );
                String str = name.substring( 0, index );
                System.out.println( "NAME AFTER CUTTING " + str );
                httpRequest( str );
            }
        }
        catch ( Exception e )
        {
            System.out.println( e.getCause() );
            System.out.println( e.getMessage() );
            System.out.println( e.getStackTrace() );
            System.out.println( e.getLocalizedMessage() );
        }

    }

    private void httpRequest( String dependencyName )
            throws IOException
    {
        URL url = new URL( "https://search.maven.org/solrsearch/"
                + "select?q=g:%22io.opentelemetry.instrumentation%22%20" + dependencyName + "&rows=20&wt=json" );
        HttpURLConnection con = ( HttpURLConnection ) url.openConnection();
        con.setRequestMethod( "GET" );
        InputStream response = con.getInputStream();

        try ( Scanner scanner = new Scanner( response ) )
        {
            String responseBody = scanner.useDelimiter( "\\A" ).next();
            System.out.println( responseBody );
        }
    }
}
