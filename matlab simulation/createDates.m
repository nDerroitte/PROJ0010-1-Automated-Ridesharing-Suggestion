function [x,y,offset] = createDates(periods,reliability,variances,noise,min_point,offset)
%period: array of integer
%reliability: a probabilitys array
%variance: array of integer.
%noise: nb of noise in the output.
max_period = max(periods);
bound = max_period*min_point;
y = zeros(1,bound);
x = 1:1:bound;
for i=1 : length(periods)
	mask = offset(i):periods(i):bound;
    proba = rand(1,length(mask));
    var = floor(-variances(i) + 2* rand(1,length(mask)) * variances(i));
    mask = mask + var;
    if length(mask) ~= length(proba) 
    disp('mask length')
    length(mask)
    disp('proba length')
    length(proba<reliability(i))
    end
    mask = mask(proba<reliability(i));
	mask(mask<=0) = [];
    mask(mask > length(y)) = [];
    y(mask) = 1;
    mask = floor(bound * rand(1,noise(i)));
    mask(mask==0) = [];
    mask(mask>length(y)) = [];
    y(mask) = 1;
end	

end