SVGS=$(wildcard images/*.svg)
SVGP=$(foreach f,$(SVGS),aux/$(basename $(notdir $(f))).pdf)
TI=./aux:$$TEXINPUTS
SRC= abstract.tex conclusion.tex experiments.tex introduction.tex root.tex sota.tex

root.pdf:  $(SRC) ieeeconf.cls $(SVGP) 
	export TEXINPUTS=$(TI); pdflatex root
	./bibtexDB.pl root
	bibtex root
	export TEXINPUTS=$(TI); pdflatex root

clean:
	rm *.aux
	rm *.bbl
	rm *.pdf
	rm *.log

$(SVGP) : aux/%.pdf : images/%.svg;
	# inkscape -E $@ $<
	- mkdir $(@D)
	inkscape -D -z --file=$<  --export-pdf=$(@:.eps=.pdf) --export-latex ;

