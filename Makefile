

default: eopa package

eopa: Makefile
	printf '#!/bin/sh\n\njava -jar %s/target/eopa-*.jar "$$@"\n' $(PWD) > eopa
	chmod +x eopa

package:
	mvn package
