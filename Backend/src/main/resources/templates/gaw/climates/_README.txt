#IGNORE

This directory contains txt files which are used to define a new climate.
When creating a new climate entry, follow the below format for proper layout

NOTE: 'i' is for integer, 'd' for double, and 's' for String. Values within ()
are the range the actual value should be within

Format:
name=s	 	      - Name of the climate type
minRoughness=i(0-100) - Minimum terrain roughness a planet of this type will have
maxRoughness=i(0-100) - Maximum terrain roughness a planet of this type will have,
			should be greater than min
popMod=d(0.05-1.0)     - Multiplier planet will use for determining final population
minMinerals=i(0-9)    - Minimum minerals a planet of this type will have
maxMinerals=i(0-9)    - Maximum minerals a planet of this type will have,
			should be greater than min