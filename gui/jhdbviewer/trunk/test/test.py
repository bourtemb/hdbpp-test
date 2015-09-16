# A python module that make calculation on HdbData and create calculated attribute
#  
# Input data value: [s,us,type,val]
# s = numer of second since epoch
# us = micro second
# type = HdbType (see org.tango.jhdb.HdbSigInfo)
# val = value (double, double array or string accroding to type, all nummerical type are converted to double)

from HDBViewer import PyHDBInterface

class PyHDBInterface(PyHDBInterface):

	def __init__(self, nb, data):

		# Input parameters
		self.nb = nb		# number of attributes
		self.data = data	# Attribute data [["Att1",[s,us,type,val],[s,us,type,val],...],["Att2",...]

		# Check input data
		if nb != 2:
			raise Exception("Invalid input argument")

		length1 = data[0][1]
		length2 = data[1][1]

		if length1 != length2:
			raise Exception("Length must be equal")

		self.length = length1

	def getResult(self):

		# Build output (formatted as input)
		# make the sum of the 2 input attributes
		sum = ["Sum",self.length]
		for i in range(0,self.length):
			t0 = self.data[0][2+i][0]
			us = self.data[0][2+i][1]
			_type = self.data[0][2+i][2]
			sval = self.data[0][2+i][3] + self.data[1][2+i][3]
			val = [t0,us,_type,sval]
			sum.append(val)

		output = [sum]
		return output
