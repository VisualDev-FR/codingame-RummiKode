import { GraphicEntityModule } from './entity-module/GraphicEntityModule.js';
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';
import { DisplayOnHoverModule } from './displayonhover-module/DisplayOnHoverModule.js'
import { TooltipModule } from './tooltip-module/TooltipModule.js';
import { ToggleModule } from './toggle-module/ToggleModule.js';

// List of viewer modules that you want to use in your game
export const modules = [
	GraphicEntityModule,
	DisplayOnHoverModule,
	EndScreenModule,	
	TooltipModule,
	ToggleModule
];

export const playerColors = [
	'#3c9fc5', // Green Circle light blue
	'#ff862d', // Green Circle light orange
	'#de6ddf', // lavender pink
	'#6ac371', // mantis green
]

export const options = [
	ToggleModule.defineToggle({
	  // The name of the toggle
	  // replace "myToggle" by the name of the toggle you want to use
	  toggle: 'drawDisplay',
	  // The text displayed over the toggle
	  title: 'DRAW DISPLAY',
	  // The labels for the on/off states of your toggle
	  values: {
		'CARD': true,
		'TEXT': false
	  },
	  // Default value of your toggle
	  default: true
	}),
  ]

export const gameName = 'RummiKode'
export const stepByStepAnimateSpeed = 3