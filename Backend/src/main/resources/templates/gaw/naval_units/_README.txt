#IGNORE

This directory contains txt files which are used to define a new naval unit.
When creating a new naval unit entry, follow the below format for proper layout

NOTE: 'i' is for integer, 'd' for double, and 's' for String. Values within ()
are the range the actual value should be within

Format:
type=s	 	      - Name of the unit type
cargoType=s	      - Name of the corresponding cargo land unit type
		      - Leave out if unit is created alongside a land unit (ie Transports)
buildTime=i(1-n)      - Number of days it takes to build ship
cost=i(0-n)	      - Money cost of unit
manpower=i(0-n)       - Manpower cost of unit, also (may in the future) affect recovery
alloys=i(0-n)         - Alloy cost of unit, also (may in the future) affect recovery
hp=i(10000's+)        - Max HP of unit, standard is 10,0000
size=i(0-9)	      - Size of ship, determines how easy ship is to hit
armor=i(0-9)	      - Ship armor, reduces damage taken (exluding boarding attacks)
evasion=i(0-9)	      - Ship evasion, reduces damage taken
firepower=i(0-9)      - Damage output of ship
range=i(0-4)	      - Used in determining which phases a ship will attack
craftHP=i(1-n)	      - Max HP of onboard spacecrafts
craftPower=i(0-9)     - Damage output of crafts
craftDefense=i(0-9)   - Basically AntiAir, used in calculating damage done to attacking craft