#
LATEX = latex
DVIPS = dvips
DVIPDF = dvipdf
PDFTEX = pdflatex
PDFJAM = pdfjam

TARGET = hdb++-design-guidelines

all : ${TARGET}.pdf

${TARGET}.pdf : ${TARGET}.tex ES.pdf CM.pdf
	${PDFTEX} ${TARGET}.tex

# ${TARGET}.pdf : ${TARGET}.dvi
# 	${DVIPDF} ${TARGET}.dvi

${TARGET}.ps : ${TARGET}.dvi
	${DVIPS} ${TARGET}.dvi

${TARGET}.dvi : ${TARGET}.tex
	${LATEX} ${TARGET}.tex

ES.pdf : FullDocument_ES.pdf
	${PDFJAM} FullDocument_ES.pdf --no-landscape --scale 0.9 --outfile ES.pdf

CM.pdf : FullDocument_CM.pdf
	${PDFJAM} FullDocument_CM.pdf --no-landscape --scale 0.9 --outfile CM.pdf

clean :
	rm -f ${TARGET}.pdf *.dvi *.log *.aux *.toc *.lot

# EOF
