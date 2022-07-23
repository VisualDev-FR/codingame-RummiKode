import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';
import { DisplayOnHoverModule } from './displayonhover-module/DisplayOnHoverModule.js'

// List of viewer modules that you want to use in your game
export const modules = [
	GraphicEntityModule,
	DisplayOnHoverModule, 
	EndScreenModule
];

export const playerColors = [
	'#3c9fc5', // Green Circle light blue
	'#ff862d', // Green Circle light orange
	'#de6ddf', // lavender pink
	'#6ac371', // mantis green
]

export const gameName = 'RummiKode'

export const stepByStepAnimateSpeed = 3