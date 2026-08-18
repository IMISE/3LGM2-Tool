# Dokumentation bearbeiten

Die Dokumentation wird als Markdown im Verzeichnis `docs` des Projekt-Repositorys
gepflegt und als GitHub-Pages-Site veröffentlicht.

## Lokale Bearbeitung

Das Repository kann lokal geklont, die Dokumentation bearbeitet und die Änderungen
anschließend per Pull Request eingereicht werden:

```
$ git clone https://github.com/IMISE/3LGM2-Tool.git
```

Die Dokumentationsseiten sind normale Dateien mit der Endung `.md`. Sie können lokal
bearbeitet und um neue Seiten ergänzt werden.

## Syntax highlighting


You can also highlight snippets of text (we use the excellent [Pygments][] library).

[Pygments]: http://pygments.org/


Here's an example of some Python code:

```
#!python

def wiki_rocks(text):
    formatter = lambda t: "funky"+t
    return formatter(text)
```


You can check out the source of this page to see how that's done, and make sure to bookmark [the vast library of Pygment lexers][lexers], we accept the 'short name' or the 'mimetype' of anything in there.
[lexers]: http://pygments.org/docs/lexers/


Have fun!
