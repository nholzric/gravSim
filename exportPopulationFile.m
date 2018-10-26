function exportPopulationFile(fileName,timeBounds,simData,orbitData)
    
    [minDiff,timeIndex] = min(abs(orbitData.time - timeBounds(1)));
    exportTime = orbitData.time(timeIndex);
    
    fprintf(1,'Writing %s at time %.0f\n',fileName,orbitData.time(timeIndex));
    
    fid = fopen(fileName,'w');
    for i = 1:size(simData.name,1)
        thisIndex = orbitData.time == exportTime & orbitData.id == simData.name(i);
        
        fprintf(fid,'NAME\t%d\n',simData.name(i));
        fprintf(fid,'POSITION\t%f\t%f\n',orbitData.p(thisIndex,:)');
        fprintf(fid,'VELOCITY\t%f\t%f\n',orbitData.v(thisIndex,:)');
        fprintf(fid,'GRAVITYMASS\t%f\n',simData.gm(i));
        if isfield(simData,'mm')
            fprintf(fid,'MYTHIUMMASS\t%f\n',simData.mm(i));
        end
    end
    fclose(fid);
end