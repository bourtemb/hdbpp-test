#
LATEX = latex
DVIPS = dvips -t a4
DVIPDF = dvipdf
PS2PDF = ps2pdf

MEETING = 2014.09.19
TARGET = ${MEETING}-hdb++-meeting

all : ${TARGET}.pdf

${TARGET}.pdf : ${TARGET}.ps
#	${DVIPDF} ${TARGET}.dvi
	${PS2PDF} ${TARGET}.ps

${TARGET}.ps : ${TARGET}.dvi
	${DVIPS} ${TARGET}.dvi

${TARGET}.dvi : ${TARGET}.tex
	${LATEX} ${TARGET}.tex

clean :
	rm -f *.dvi *.aux *.lot *.log *.toc ${TARGET}.ps ${TARGET}.pdf

# EOF
