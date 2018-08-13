units lj
dimension 3
boundary p p p
atom_style atomic
lattice sc 0.8442
region box block 0 5 0 5 0 5
create_box 1 box
create_atoms 1 box
mass 1 1.0
velocity all create 0.728 SEED
velocity all zero linear
velocity all scale 1.50
pair_style soft 1.122
pair_coeff 1 1 10
neighbor 0.3 bin
neigh_modify every 20 delay 0 check no
fix 1 all nve
timestep 0.001
thermo 50
run 5000
velocity all zero linear
velocity all scale 1.50
run 5000
velocity all zero linear
velocity all scale 1.50
run 5000
velocity all zero linear
velocity all scale 1.50
run 5000
dump id all custom 10 melt.dump id type xu yu zu vx vy vz
run NUMTS`'0
